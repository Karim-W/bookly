package com.cmp354.bookly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_name, et_email, et_phone, et_password, et_password_confirm;
    Button btn_login;
    ImageButton btn_register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.orange1));

        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);
        et_password_confirm = findViewById(R.id.et_password_confirm);
        btn_register = findViewById(R.id.btn_register);
        btn_login = findViewById(R.id.btn_login);

        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);


    }

    private boolean validateFields(){
        String name = et_name.getText().toString();
        if(name.isEmpty()){
            Toast.makeText(this, "Name field is empty!", Toast.LENGTH_SHORT).show();
            return false;
        }

        String email = et_email.getText().toString();
        if(email.isEmpty()){
            Toast.makeText(this, "Email field is empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(!email.matches(emailPattern)){
            Toast.makeText(this, "Not a valid email!", Toast.LENGTH_SHORT).show();
            return false;
        }
        String phone = et_phone.getText().toString();
        if(phone.isEmpty()){
            Toast.makeText(this, "Phone field is empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        String password = et_password.getText().toString();
        String password2 = et_password_confirm.getText().toString();

        if(password.isEmpty() || password2.isEmpty()){
            Toast.makeText(this, "Password field is empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!password.equals(password2)){
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return false;
        }

        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        databaseConnector.open();
        Cursor result = databaseConnector.getOneUserByEmail(email);
        if(result.moveToFirst()){
            Toast.makeText(this, "Email is already registered!", Toast.LENGTH_SHORT).show();
            databaseConnector.close();
            return false;
        }
        else{
            databaseConnector.insertUser(name,email,phone,password,0);
            databaseConnector.close();
            Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show();
            return true;
        }

    }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_login){
            Intent intentLogin = new Intent(this, LoginActivity.class);
            startActivity(intentLogin);
        }
        else if (v.getId() == R.id.btn_register){
            if(validateFields()){
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }
}