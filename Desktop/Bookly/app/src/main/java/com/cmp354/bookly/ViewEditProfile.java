package com.cmp354.bookly;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class ViewEditProfile extends AppCompatActivity implements View.OnClickListener {

    Button btn_save;
    TextInputEditText name, email, phone, password;
    int user_id, posts;
    String oldName, oldEmail, oldPhone, oldPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_profile);


        Intent intent = getIntent();
        user_id = intent.getIntExtra("userID",-1);

        btn_save = findViewById(R.id.btn_save);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);

        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        databaseConnector.open();
        Cursor result = databaseConnector.getOneUser(user_id);

        if(result.moveToFirst()){
            int n = result.getColumnIndex("name");
            int e =  result.getColumnIndex("email");
            int p =  result.getColumnIndex("phone");
            int po = result.getColumnIndex("posts");
            int pa = result.getColumnIndex("password");
            oldName = result.getString(n);
            oldEmail = result.getString(e);
            oldPhone = result.getString(p);
            oldPass = result.getString(pa);
            posts = result.getInt(po);
            name.setText(oldName);
            email.setText(oldEmail);
            phone.setText(oldPhone);
        }
        databaseConnector.close();

        btn_save.setOnClickListener(this);
    }

    private boolean validateFields(){
        boolean changes = false;
        String n =  name.getText().toString();
        if(n.isEmpty()){
            name.setError("Name field cannot be left empty!");
            return false;
        }
        if(!n.equals(oldName)) changes = true;
        String e =  email.getText().toString();
        if(e.isEmpty()){
            email.setError("Email field cannot be left empty!");
            return false;
        }
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(!e.matches(emailPattern)){
            email.setError("Email is invalid!");
            return false;
        }
        if(!e.equals(oldEmail)) changes = true;
        String p =  phone.getText().toString();
        if(p.isEmpty()){
            phone.setError("Phone field cannot be left empty!");
            return false;
        }
        if(!p.equals(oldPhone)) changes = true;
        String pass =  password.getText().toString();
        if(pass.isEmpty()){
            pass=oldPass;
        }
        else {
            if (!p.equals(oldPass)) changes = true;
        }

        if(!changes){
            Toast.makeText(this, "No changes were made to update details!", Toast.LENGTH_SHORT).show();
        }
        else {
            DatabaseConnector databaseConnector = new DatabaseConnector(this);
            databaseConnector.updateUser(user_id, n, e, p, pass, posts);
            Toast.makeText(this, "Profile was updated successfully!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_save){
            validateFields();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //convert XML to java object
        getMenuInflater().inflate(R.menu.menu_profile, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.item_delete) {
            // create a new AlertDialog Builder
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this); //TS: U21: getApplicationContext caused an error here!

            builder.setTitle("Delete Account"); // title bar string
            builder.setMessage("Are you sure you want to permanently delete your account?"); // message to display

            // provide an OK button that simply dismisses the dialog
            builder.setPositiveButton("Delete",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int button)
                        {
                            DatabaseConnector databaseConnector =
                                    new DatabaseConnector(ViewEditProfile.this);

                            databaseConnector.deleteUser(user_id);

                            finish(); // return to the AddressBook Activity
                        }
                    }
            );

            builder.setNegativeButton("Cancel", null);
            builder.show(); // display the Dialog
        }
        return true;
    }
}