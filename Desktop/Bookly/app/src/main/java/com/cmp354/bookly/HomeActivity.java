package com.cmp354.bookly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottom_nav;
    FloatingActionButton fab;
    int user_id;
    TextView textHello;
    ImageView iv_profile;
    private BookReaderApp app;
    private RSSFeed feed;
    private long feedPubDateMillis;
    private FileIO io;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        io = new FileIO(getApplicationContext());
        new DownloadFeed().execute();
        Intent intent = getIntent();
        user_id = intent.getIntExtra("userID",-1);
        iv_profile = findViewById(R.id.iv_profile);
        iv_profile.setOnClickListener(this);

        // get references to Application and FileIO objects
       // app = (BookReaderApp) getApplication();



        textHello = findViewById(R.id.textHello);

        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        //databaseConnector.open();
        //databaseConnector.deleteBook(1);
        //databaseConnector.close();
        databaseConnector.open();
        Cursor result = databaseConnector.getOneUser(user_id);

        if(result.moveToFirst()){
            int idx = result.getColumnIndex("name");
            String name = result.getString(idx);
            textHello.setText("Hello, " + name);
        }
        databaseConnector.close();

        bottom_nav = findViewById(R.id.bottom_nav);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

        bottom_nav.setOnNavigationItemSelectedListener(this);


        //new ReadFeed().execute();


        ViewPager2 RecentBooksViewPager = findViewById(R.id.RecentbooksViewPager);
        ArrayList<Book> books = databaseConnector.getAllBooks();

        RecentBooksViewPager.setAdapter(new BookSliderAdapter(books));
        RecentBooksViewPager.setClipToPadding(false);
        RecentBooksViewPager.setClipChildren(false);
        RecentBooksViewPager.setOffscreenPageLimit(3);
        RecentBooksViewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer=new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer(){
            @Override
            public void transformPage(@NonNull View page,float position){
                float r =1-Math.abs(position);
                page.setScaleY(0.95f+r*0.05f);
            }
        });

        RecentBooksViewPager.setPageTransformer(compositePageTransformer);

    }

    class DownloadFeed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            io.downloadFile();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("Book reader", "Feed downloaded");
            new ReadFeed().execute();
        }
    }

    class ReadFeed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            feed = io.readFile();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("Book reader", "Feed read");
            if (app!=null && feed!=null) app.setFeedMillis(feed.getPubDateMillis());
            updateDisplay();
        }
    }

    public void updateDisplay()
    {
        ViewPager2 booksViewPager = findViewById(R.id.booksViewPager);
        ArrayList<Book> books = new ArrayList<Book>();

        List<RSSItem> items = feed.getAllItems();

        for(RSSItem item : items){
            Book b = new Book();
            String t = item.getTitle();
            t = (t.length() > 25)? t.substring(0,24)+"..." : t;
            b.title = t;
            b.author = "";
            b.year = Integer.parseInt(item.getPubDateFormatted());
            b.uri = Uri.parse(item.getImageURL());

            books.add(b);
        }

        booksViewPager.setAdapter(new BookSliderAdapter(books));
        booksViewPager.setClipToPadding(false);
        booksViewPager.setClipChildren(false);
        booksViewPager.setOffscreenPageLimit(3);
        booksViewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer=new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(0));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer(){
            @Override
            public void transformPage(@NonNull View page,float position){
                float r =1-Math.abs(position);
                page.setScaleY(0.95f+r*0.05f);
            }
        });

        booksViewPager.setPageTransformer(compositePageTransformer);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        if(item.getItemId() == R.id.Search){
            Intent intent = new Intent(this, BookListActivity.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab){
            Intent addBook = new Intent(this, AddEditBookActivity.class);
            addBook.putExtra("userID",user_id);
            startActivity(addBook);
        }
        else if(v.getId() == R.id.iv_profile){
            Intent profile = new Intent(this, ProfileActivity.class);
            profile.putExtra("userID",user_id);
            startActivity(profile);
        }
    }
}