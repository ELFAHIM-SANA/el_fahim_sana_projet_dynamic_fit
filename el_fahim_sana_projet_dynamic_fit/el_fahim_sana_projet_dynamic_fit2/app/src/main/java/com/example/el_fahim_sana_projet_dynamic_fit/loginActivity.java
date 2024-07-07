package com.example.el_fahim_sana_projet_dynamic_fit;



import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class loginActivity extends AppCompatActivity {

    EditText email,password;
    Button save,login;
    DBHelper DB;
    boolean passwordVesibility=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        login = findViewById(R.id.login_button);
        save = findViewById(R.id.SignUp_button);

        DB = new DBHelper(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = email.getText().toString();
                String pass = password.getText().toString() ;

                if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) { //verifier si l'un des champs est vide
                    if(TextUtils.isEmpty(user)) {
                        Toast.makeText(loginActivity.this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show();
                        email.setError("entrer l'email");
                    }else{
                        Toast.makeText(loginActivity.this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show();
                        password.setError("entrer password");
                    }
                }
                else {
                    Boolean checkuser = DB.checkusername(user);
                    if (!checkuser){
                        Boolean checkuserpass = DB.checkusernamepassword(user,pass);
                        if (checkuserpass){
                            Variables.email =user;
                            Variables.id=DB.getId(user);
                            Toast.makeText(loginActivity.this,"login réussi",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(loginActivity.this,"password incorrect",Toast.LENGTH_SHORT).show();
                            password.setError("password incorrect");
                        }
                    }else{
                        Toast.makeText(loginActivity.this,"email incorrect",Toast.LENGTH_SHORT).show();
                        email.setError("email incorrect");
                    }
                }
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(!passwordVesibility){
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24,0,R.drawable.show_password,0);
                            passwordVesibility=true;
                        }else {
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24,0,R.drawable.hide_password,0);
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVesibility=false;
                        }

                        return true;
                    }
                }
                return false;
            }
        });

    }
}