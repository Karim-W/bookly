package com.cmp354.bookly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PostsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, TextView.OnEditorActionListener {

    int user_id;
    ListView lv_books;
    EditText et_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        Intent intent = getIntent();
        user_id = intent.getIntExtra("userID",-1);

        lv_books = findViewById(R.id.lv_books);
        et_search = findViewById(R.id.et_search);
        et_search.setOnEditorActionListener(this);

        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        ArrayList<Book> books = databaseConnector.getAllBooks();

        ArrayList<Book> Mybooks = new ArrayList<Book>();

        for(Book b : books){
            if(b.user_id == user_id){
                Mybooks.add(b);
            }
        }

        PostAdapter adapter = new PostAdapter(this, R.layout.my_post_adapter, Mybooks);
        lv_books.setAdapter(adapter);

        lv_books.setOnItemClickListener(this);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Book book = (Book) parent.getAdapter().getItem(position);

        Intent intent = new Intent(this, Book_Activity.class);
        intent.putExtra("id",book.id);
        startActivity(intent);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(v.getId() == R.id.et_search){
            String search = et_search.getText().toString();
            if(!search.isEmpty()){
                DatabaseConnector databaseConnector = new DatabaseConnector(this);
                ArrayList<Book> books = databaseConnector.SearchBooks(search);

                ArrayList<Book> Mybooks = new ArrayList<Book>();
                for(Book b : books){
                    if(b.user_id == user_id){
                        Mybooks.add(b);
                    }
                }
                PostAdapter adapter = new PostAdapter(this, R.layout.my_post_adapter, Mybooks);
                lv_books.setAdapter(adapter);

                lv_books.setOnItemClickListener(this);
                return false;
            }
            else{
                DatabaseConnector databaseConnector = new DatabaseConnector(this);
                ArrayList<Book> books = databaseConnector.getAllBooks();

                ArrayList<Book> Mybooks = new ArrayList<Book>();

                for(Book b : books){
                    if(b.user_id == user_id){
                        Mybooks.add(b);
                    }
                }

                PostAdapter adapter = new PostAdapter(this, R.layout.my_post_adapter, Mybooks);
                lv_books.setAdapter(adapter);

                lv_books.setOnItemClickListener(this);
            }
        }
        return false;
    }
}