package com.cmp354.bookly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    EditText et_email, et_password;
    Button btn_register;
    ImageButton btn_login;
    SharedPreferences sharedPreferences;
    CheckBox cb_remember;
    int user_id;
    boolean saveDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_id = -1;
        saveDetails = false;
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        cb_remember = findViewById(R.id.cb_remember);
        btn_register = findViewById(R.id.btn_register);
        getSupportActionBar().hide();
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue1));

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        cb_remember.setOnCheckedChangeListener(this);

        sharedPreferences = getSharedPreferences("SavedValues", MODE_PRIVATE);

        checkSavedDetails();

    }

    private void checkSavedDetails(){
        int id = sharedPreferences.getInt("userID", -1);
        if(id == -1) return;
        String email = sharedPreferences.getString("email", null);
        String password = sharedPreferences.getString("password", null);

        if(validateCredentials(email, password)){
            Intent intentLogin = new Intent(this, HomeActivity.class);
            intentLogin.putExtra("userID", user_id);
            startActivity(intentLogin);
        }
    }

    private boolean validateCredentials(String email, String password){
        if(email.isEmpty()){
            Toast.makeText(this, "Email field is empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (password.isEmpty()){
            Toast.makeText(this, "Password field is empty!", Toast.LENGTH_SHORT).show();
        }

        DatabaseConnector databaseConnector = new DatabaseConnector(this);
        databaseConnector.open();
        Cursor result = databaseConnector.getOneUserByEmail(email);

        if(result.moveToFirst()){
            int passIndex = result.getColumnIndex("password");
            if(password.equals(result.getString(passIndex))){
                user_id = result.getInt(0);
                if(saveDetails){
                    SharedPreferences.Editor e = sharedPreferences.edit();
                    e.putInt("userID",user_id);
                    e.putString("email",email);
                    e.putString("password",password);
                    e.commit();
                }
                databaseConnector.close();
                return true;
            }
        }
        else{
            databaseConnector.close();
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_register){
            Intent intentRegister = new Intent(this, RegisterActivity.class);
            startActivity(intentRegister);
        }
        else if (v.getId() == R.id.btn_login){
            String email = et_email.getText().toString();
            String password = et_password.getText().toString();
            if(validateCredentials(email,password)){
                Intent intentLogin = new Intent(this, HomeActivity.class);
                intentLogin.putExtra("userID", user_id);
                startActivity(intentLogin);
            }
            else{
                Toast.makeText(this, "Invalid Login!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.cb_remember){
            saveDetails = isChecked;
        }
    }
}