package com.cmp354.bookly;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class AddEditBookActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int GET_FROM_GALLERY = 3;
    Bitmap bitmap;
    ImageView iv_book;
    TextView tv_upload;
    Button btn_post;
    TextInputEditText title_et, author_et, year_et, isbn_et, price_et, description_et;
    AutoCompleteTextView category_ac;
    int user_id;
    ArrayList<String> categories;
    int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_book);
        bitmap = null;
        Intent intent = getIntent();
        user_id = intent.getIntExtra("userID", -1);
        
        iv_book = findViewById(R.id.iv_book);
        tv_upload = findViewById(R.id.tv_upload);
        btn_post = findViewById(R.id.btn_post);

        title_et = findViewById(R.id.title);
        author_et = findViewById(R.id.author);
        year_et = findViewById(R.id.year);
        isbn_et = findViewById(R.id.isbn);
        price_et = findViewById(R.id.price);
        description_et = findViewById(R.id.description);
        category_ac = findViewById(R.id.category);
        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        databaseConnector.addCategory();
        categories = databaseConnector.getAllCategories();


        ArrayAdapter<String> adapter =new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,categories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_ac.setAdapter(adapter);

        category_ac.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index = position;
            }
        });

        iv_book.setOnClickListener(this);
        btn_post.setOnClickListener(this);

    }

    private boolean checkFields(){
        String title = title_et.getText().toString();
        boolean error = false;
        if(title.isEmpty()){
            title_et.setError("Book title field cannot be empty!");
            error = true;
        }
        String author = author_et.getText().toString();
        if(author.isEmpty()){
            author_et.setError("Author field cannot be empty!");
            error = true;
        }
        String year = year_et.getText().toString();
        if(year.isEmpty()){
            year_et.setError("Year field cannot be empty!");
            error = true;
        }
        String isbn = isbn_et.getText().toString();
        if(isbn.isEmpty()){
            isbn_et.setError("ISBN field cannot be empty!");
            error = true;
        }
        String price = price_et.getText().toString();
        if(price.isEmpty()){
            price_et.setError("Price field cannot be empty!");
            error = true;
        }
        String description = description_et.getText().toString();
        if(description.isEmpty()){
            description_et.setError("Description field cannot be empty!");
            error = true;
        }

        if(error){
            return false;
        }

        DatabaseConnector databaseConnector = new DatabaseConnector(this);

        String cat = categories.get(index);
        int category = databaseConnector.categoryPosition(cat);

        databaseConnector.insertBook(user_id,title,author,Integer.parseInt(year),category,isbn,Float.parseFloat(price),description,bitmap);
        databaseConnector.incrementPosts(user_id);
        Toast.makeText(this, "Book was posted successfully!", Toast.LENGTH_SHORT).show();

        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_book){
            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
        }
        else if (v.getId() == R.id.btn_post){
            if(checkFields()){
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("userID", user_id);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                iv_book.setImageBitmap(Bitmap.createScaledBitmap(bitmap,120,180,false));
                tv_upload.setVisibility(View.GONE);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}