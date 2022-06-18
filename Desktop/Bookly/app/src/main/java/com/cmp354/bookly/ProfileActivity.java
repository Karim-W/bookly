package com.cmp354.bookly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_name, tv_posts, tv_edit, tv_fav, tv_total_posts;
    int user_id;
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        user_id = intent.getIntExtra("userID", -1);


        tv_name = findViewById(R.id.tv_name);
        tv_posts = findViewById(R.id.tv_posts);
        tv_edit = findViewById(R.id.tv_edit);
        tv_fav = findViewById(R.id.tv_fav);
        tv_total_posts = findViewById(R.id.tv_total_posts);
        back = findViewById(R.id.back);

        back.setOnClickListener(this);
        tv_edit.setOnClickListener(this);
        tv_posts.setOnClickListener(this);
        tv_fav.setOnClickListener(this);

        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        databaseConnector.open();
        Cursor result = databaseConnector.getOneUser(user_id);

        if(result.moveToFirst()){
            int idx = result.getColumnIndex("name");
            int p =  result.getColumnIndex("posts");
            String name = result.getString(idx);
            int posts = result.getInt(p);
            tv_name.setText(name);
            tv_total_posts.setText(posts + " Posts");
        }
        databaseConnector.close();


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tv_posts){
            Intent posts = new Intent(this, PostsActivity.class);
            posts.putExtra("userID", user_id);
            startActivity(posts);
        }
        else if (v.getId() == R.id.tv_fav){

        }
        else if (v.getId() == R.id.tv_edit){
            Intent edit = new Intent(this, ViewEditProfile.class);
            edit.putExtra("userID", user_id);
            startActivity(edit);

        }
        else if(v.getId() == R.id.back){
            finish();
        }
    }
}