package com.cmp354.bookly;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Book_Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_title,tv_info,tv_description;
    private ImageView iv_poster;
    private Button btn_price, btn_call;
    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_activity);
        getSupportActionBar().hide();
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_info = (TextView) findViewById(R.id.tv_info);
        iv_poster = (ImageView) findViewById(R.id.iv_poster);
        btn_price = (Button) findViewById(R.id.btn_price);
        btn_call = (Button) findViewById(R.id.btn_call);

        btn_call.setOnClickListener(this);
        // Recieve data
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        databaseConnector.open();
        phone = databaseConnector.getPhone(id);
        databaseConnector.close();

        databaseConnector.open();
        Cursor result = databaseConnector.getOneBook(id);


        if(result.moveToFirst()){
            int title = result.getColumnIndex("title");
            int description = result.getColumnIndex("description");
            int category = result.getColumnIndex("BookCategory__id");
            int image = result.getColumnIndex("image");
            int price = result.getColumnIndex("price");
            btn_price.setText(result.getString(price) + " AED");
            tv_title.setText(result.getString(title));
            tv_description.setText(result.getString(description));
            byte [] s = result.getBlob(image);

            tv_info.setText(databaseConnector.getCategory(result.getInt(category)));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeByteArray(s,0,s.length,new BitmapFactory.Options());

            iv_poster.setImageBitmap(bitmap);

        }


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_call){
            String number = "tel:"+phone;
            Uri callUri = Uri.parse(number);
            // create the intent and start it
            Intent callIntent = new Intent(Intent.ACTION_DIAL, callUri);
            startActivity(callIntent);
        }
    }
}
