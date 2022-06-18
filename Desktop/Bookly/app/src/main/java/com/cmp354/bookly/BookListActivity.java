package com.cmp354.bookly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    List<Book> lstBook ;
    EditText et_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().hide();
        et_search = findViewById(R.id.et_search);
        et_search.setOnEditorActionListener(this);
        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        lstBook = databaseConnector.getAllBooks();

        RecyclerView myrv = (RecyclerView) findViewById(R.id.rv);
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(this,lstBook);
        myrv.setLayoutManager(new GridLayoutManager(this,3));
        myrv.setAdapter(myAdapter);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(v.getId() == R.id.et_search){
            if(!et_search.getText().toString().isEmpty()) {
                DatabaseConnector databaseConnector = new DatabaseConnector(this);
                ArrayList<Book> books = databaseConnector.SearchBooks(et_search.getText().toString());

                RecyclerView myrv = (RecyclerView) findViewById(R.id.rv);
                RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(this,books);
                myrv.setLayoutManager(new GridLayoutManager(this,3));
                myrv.setAdapter(myAdapter);

            }
            else{
                DatabaseConnector databaseConnector = new DatabaseConnector(this);
                ArrayList<Book> lstBook = databaseConnector.getAllBooks();

                RecyclerView myrv = (RecyclerView) findViewById(R.id.rv);
                RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(this,lstBook);
                myrv.setLayoutManager(new GridLayoutManager(this,3));
                myrv.setAdapter(myAdapter);
            }
        }
        return false;
    }
}