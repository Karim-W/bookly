// DatabaseConnector.java
// Provides easy connection and creation of UserContacts database.
package com.cmp354.bookly;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Base64;

//sql command -> reusable code
public class DatabaseConnector {
   // database name
   private static final String DATABASE_NAME = "Bookly";
   // where you will run SQL commands
   private SQLiteDatabase database; // TS: to run SQL commands
   //to open or create database
   private DatabaseOpenHelper databaseOpenHelper; // TS: create or open the database
   Context context;
   // public constructor for DatabaseConnector
   public DatabaseConnector(Context context) {
      try {
         Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
         field.setAccessible(true);
         field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
      } catch (Exception e) {
         e.printStackTrace();
      }
      // create a new DatabaseOpenHelper
      databaseOpenHelper =
              new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
      this.context = context;
   } // end DatabaseConnector constructor

   // open the database connection
   public void open() throws SQLException {
      // create or open a database for reading/writing
      database = databaseOpenHelper.getWritableDatabase();//TS: at the first call, onCreate is called
   }

   // close the database connection
   public void close() {
      if (database != null)
         database.close(); // close the database connection
   }

   // no id is needed -> auto-increment
   public void insertUser(String name, String email, String phone,
                             String password, int posts) {
      //sequence of key value pairs
      ContentValues newUser = new ContentValues();
      //names between double quotation must match table columns
      newUser.put("name", name);
      newUser.put("email", email);
      newUser.put("phone", phone);
      newUser.put("password", password);
      newUser.put("posts", posts);

      open(); // open the database
      database.insert("users", null, newUser);
      close(); // close the database
   }

   public void insertBook(long id, String title, String author, int year, int category, String isbn, float price,
                          String description, Bitmap bitmap) {


      /*
      int size = bitmap.getRowBytes() * bitmap.getHeight();
      ByteBuffer byteBuffer = ByteBuffer.allocate(size);
      bitmap.copyPixelsToBuffer(byteBuffer);
      byte [] byteArray = Base64.getEncoder().encode(byteBuffer.array());*/

      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG,0,stream);
      byte [] byteArray = stream.toByteArray();

      //sequence of key value pairs
      ContentValues newBook = new ContentValues();
      //names between double quotation must match table columns
      newBook.put("title", title);
      newBook.put("description", description);
      newBook.put("ISBN", isbn);
      newBook.put("price", price);
      newBook.put("rating", 0);
      newBook.put("author", author);
      newBook.put("year", year);
      newBook.put("users__id",id);
      newBook.put("image",byteArray);
      //newBook.put("category",category);
      newBook.put("latitude",0);
      newBook.put("longitude",0);
      newBook.put("BookCategory__id",category);
      open(); // open the database
      database.insert("books", null, newBook);
      close(); // close the database
   }

   // need the ID to identify which record to update
   public void updateUser(long id, String name, String email, String phone,
                          String password, int posts) {
      //sequence of key value pairs
      ContentValues editUser = new ContentValues();
      //names between double quotation must match table columns
      editUser.put("name", name);
      editUser.put("email", email);
      editUser.put("phone", phone);
      editUser.put("password", password);
      editUser.put("posts", posts);

      open(); // 1. open the database
      // update
      // table name, content value, where clause, where args must be String []
      database.update("users", editUser, "_id=" + id, null);
      close(); // close the database
   }

   // return a Cursor with all contact information in the database
   // cursor -> pointer to the databAse
   // placed before the first record -> must be moved
   public Cursor getAllUsers() {
      //return database.query("contacts", new String[] {"_id", "name"},
      //	         null, null, null, null, "name"/*order by*/);
      return database.query("users", new String[]{"_id", "name"},
              null, null, null, null, "name DESC"/*order by Descending*/);
   }

   // get a Cursor containing all information about the contact specified
   // by the given id
   //for functions that return a Cursor, open and close are inside the application
   public Cursor getOneUser(long id) {

      return database.query("users", null/*get all fields*/,
              "_id=" + id /*selection*/, null, null, null, null);

      //TS: OR
      //return database.rawQuery("SELECT * FROM contacts WHERE _id = " + String.valueOf(id)  , null);
   }

   public Cursor getOneBook(long id) {

      return database.query("books", null/*get all fields*/,
              "_id=" + id /*selection*/, null, null, null, null);
   }

   public Cursor getOneUserByEmail(String email) {

      return database.query("users", null/*get all fields*/,
              "email='" + email + "'" /*selection*/, null, null, null, null);

      //TS: OR
      //return database.rawQuery("SELECT * FROM contacts WHERE _id = " + String.valueOf(id)  , null);

   }

   public String getCategory (long id){
      open();
      Cursor result = database.query("BookCategory", null/*get all fields*/,
                 "_id=" + id /*selection*/, null, null, null, null);

      if(result.moveToFirst()) {
         String c = result.getString(1);
         close();
         return c;
      }
      close();
      return null;
   }

   public String getPhone (long id){
      open();
      Cursor result = database.query("books", null/*get all fields*/,
              "_id=" + id /*selection*/, null, null, null, null);

      if(result.moveToFirst()) {
         int user = result.getInt(8);

         Cursor c = database.query("users", null/*get all fields*/,
                 "_id=" + user /*selection*/, null, null, null, null);

         if(c.moveToFirst()){
            int p = c.getColumnIndex("phone");
            String phone = c.getString(p);
            close();
            return phone;
         }
      }
      close();
      return null;
   }

   public void incrementPosts(long id){
      open();
      Cursor c = database.query("users", null/*get all fields*/,
              "_id=" + id /*selection*/, null, null, null, null);

      if(c.moveToFirst()){
         int n = c.getColumnIndex("name");
         int e = c.getColumnIndex("email");
         int ph = c.getColumnIndex("phone");
         int pa = c.getColumnIndex("password");
         int po = c.getColumnIndex("posts");

         String name = c.getString(n);
         String email = c.getString(e);
         String phone = c.getString(ph);
         String password = c.getString(pa);
         int posts = c.getInt(po);

         close();

         updateUser(id,name,email,phone,password,++posts);

      }
   }
   public ArrayList<Book> SearchBooks(String title){
      ArrayList<Book> books = new ArrayList<Book>();
      open();
      Cursor c = database.rawQuery("SELECT * FROM books WHERE title like  '%" + title + "%'"  , null);
      if (c.moveToFirst()){
         do{
            Book b = new Book();
            b.id = c.getInt(0);
            b.title = c.getString(1);
            b.description = c.getString(2);
            b.ISBN = c.getString(3);
            b.price = c.getFloat(4);
            b.rating = c.getInt(5);
            b.author = c.getString(6);
            b.year = c.getInt(7);
            b.user_id = c.getInt(8);
            byte [] s = c.getBlob(9);


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeByteArray(s,0,s.length,new BitmapFactory.Options());


            b.image = bitmap;
            b.category = c.getInt(10);
            b.latitude = c.getFloat(11);
            b.longitude = c.getInt(12);

            books.add(b);

         }while(c.moveToNext());
      }

      close();
      return books;
   }

   // delete the contact specified by the given String name
   public void deleteUser(long id) {
      ArrayList<Book> books = getAllBooks();
      for(Book b : books){
         if(b.user_id == id){
            deleteBook(b.id);
         }
      }
      open();
      database.delete("users", "_id=" + id, null);
      /*OR*/ //database.delete("contacts", "_id = ?", new String[] {String.valueOf(id)});
      close();
   }

   public void deleteBook(long id) {
      open();
      database.delete("books", "_id=" + id, null);
      /*OR*/ //database.delete("contacts", "_id = ?", new String[] {String.valueOf(id)});
      close();
   }

   public ArrayList<String> getAllContactsInAListOfStrings()
   {
      String rst = "";
      ArrayList<String> listOfContacts = new ArrayList<String>();

      open();
      Cursor c = database.query("contacts", null,null, null, null, null, "name DESC"/*order by*/);

      //points before the first point so it must be moved
      //check that result is not empty
      if (c.moveToFirst()){
         do{
            rst += c.getString(1)+"\n";//name
            rst += c.getString(2)+"\n";//email
            rst += c.getString(3)+"\n";//phone
            rst += c.getString(4)+"\n";//street
            rst += c.getString(5)+"\n";//city
            rst += "--------------------\n";
            listOfContacts.add(rst);
            rst = "";

         }while(c.moveToNext());
      }

      close();
      return listOfContacts;
   }

   public ArrayList<Book> getRecentBooks(){
      ArrayList<Book> books = getAllBooks();
      ArrayList<Book> returnbooks;
      if(books.size() > 10){
         returnbooks = new ArrayList<Book>();
         for(int i =0; i<10; i++){
            returnbooks.add(books.get(i));
         }
      }
      else{
         returnbooks = books;
      }

      return returnbooks;
   }

   public ArrayList<Book> getAllBooks()
   {
      String rst = "";
      ArrayList<Book> books = new ArrayList<Book>();

      open();
      Cursor c = database.query("books", null,null, null, null, null, null);

      //points before the first point so it must be moved
      //check that result is not empty
      if (c.moveToFirst()){
         do{
            Book b = new Book();
            b.id = c.getInt(0);
            b.title = c.getString(1);
            b.description = c.getString(2);
            b.ISBN = c.getString(3);
            b.price = c.getFloat(4);
            b.rating = c.getInt(5);
            b.author = c.getString(6);
            b.year = c.getInt(7);
            b.user_id = c.getInt(8);
            byte [] s = c.getBlob(9);


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeByteArray(s,0,s.length,new BitmapFactory.Options());


            b.image = bitmap;
            b.category = c.getInt(10);
            b.latitude = c.getFloat(11);
            b.longitude = c.getInt(12);

            books.add(b);

         }while(c.moveToNext());
      }

      close();
      return books;
   }

   public ArrayList<String> getAllCategories()
   {
      String rst = "";
      ArrayList<String> categories = new ArrayList<String>();

      open();
      Cursor c = database.query("BookCategory", null,null, null, null, null, null);

      //points before the first point so it must be moved
      //check that result is not empty
      if (c.moveToFirst()){
         do{
            rst += c.getString(1);//name
            categories.add(rst);
            rst = "";

         }while(c.moveToNext());
      }

      close();
      return categories;
   }

   public void addCategory(){
      //sequence of key value pairs
      ContentValues newCat = new ContentValues();
      //names between double quotation must match table columns
      newCat.put("category", "Fiction");

      open(); // open the database
      database.insert("BookCategory", null, newCat);
      close(); // close the database

      //sequence of key value pairs
      newCat = new ContentValues();
      //names between double quotation must match table columns
      newCat.put("category", "Non-Fiction");

      open(); // open the database
      database.insert("BookCategory", null, newCat);
      close(); // close the database

      //sequence of key value pairs
      newCat = new ContentValues();
      //names between double quotation must match table columns
      newCat.put("category", "Educational");

      open(); // open the database
      database.insert("BookCategory", null, newCat);
      close(); // close the database
   }

   public int categoryPosition(String category){
      open();
      int idx = -1;
      Cursor result = database.query("BookCategory", null/*get all fields*/,
              "category='" + category + "'" /*selection*/, null, null, null, null);

      if(result.moveToFirst()){
         idx = result.getInt(0);
      }
      close();

      return idx;

   }


   private class DatabaseOpenHelper extends SQLiteOpenHelper {
      // public constructor
      public DatabaseOpenHelper(Context context, String name,
                                CursorFactory factory, int version) {
         super(context, name, factory, version);
      }

      // creates the contacts table when the database is created
      // TS: this is called from  open()->getWritableDatabase(). Only if the database does not exist
      //called only once, teh first time db is opened
      @Override
      public void onCreate(SQLiteDatabase db) {
         // query to create a new table named contacts
         String createQuery = "CREATE TABLE BookCategory (\n" +
                 "    _id integer NOT NULL CONSTRAINT BookCategory_pk PRIMARY KEY,\n" +
                 "    category text NOT NULL\n" +
                 ");";

         db.execSQL(createQuery);

         createQuery = "CREATE TABLE users (\n" +
                 "    _id integer NOT NULL CONSTRAINT users_pk PRIMARY KEY autoincrement,\n" +
                 "    name text NOT NULL,\n" +
                 "    email text NOT NULL,\n" +
                 "    phone text NOT NULL,\n" +
                 "    password text NOT NULL,\n" +
                 "    posts integer NOT NULL DEFAULT 0\n" +
                 ");";

         db.execSQL(createQuery);

        createQuery = "CREATE TABLE books (\n" +
                "    _id integer NOT NULL CONSTRAINT books_pk PRIMARY KEY AUTOINCREMENT,\n" +
                "    title text NOT NULL,\n" +
                "    description text NOT NULL,\n" +
                "    ISBN text,\n" +
                "    price float NOT NULL,\n" +
                "    rating integer,\n" +
                "    author text NOT NULL,\n" +
                "    year integer,\n" +
                "    users__id integer NOT NULL,\n" +
                "    image blob NOT NULL,\n" +
                "    BookCategory__id integer NOT NULL,\n" +
                "    latitude float NOT NULL,\n" +
                "    longitude integer NOT NULL,\n" +
                "    CONSTRAINT books_users FOREIGN KEY (users__id)\n" +
                "    REFERENCES users (_id),\n" +
                "    CONSTRAINT books_BookCategory FOREIGN KEY (BookCategory__id)\n" +
                "    REFERENCES BookCategory (_id)\n" +
                ");";

         //query is a string
         db.execSQL(createQuery); // execute the query
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion,
                            int newVersion) {
      }
   }
}


/**************************************************************************
 * (C) Copyright by Deitel & Associates, Inc. and                         *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 **************************************************************************/
