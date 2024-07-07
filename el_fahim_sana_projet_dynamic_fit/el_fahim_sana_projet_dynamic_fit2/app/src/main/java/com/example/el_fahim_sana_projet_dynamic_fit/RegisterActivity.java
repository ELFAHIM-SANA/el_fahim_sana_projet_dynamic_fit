package com.example.el_fahim_sana_projet_dynamic_fit;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {

    EditText email,password,repassword;
    Button save,login;

    DBHelper DB;

    boolean passwordVesibility=false;
    boolean repasswordVesibility=false;
    @SuppressLint({"WrongThread", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email=findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        repassword = findViewById(R.id.signup_repassword);
        save =findViewById(R.id.signup_button);
        login = findViewById(R.id.loginn_button);

        DB = new DBHelper(this);

        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.iconepersonne);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] image = stream.toByteArray();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = email.getText().toString();
                String pass = password.getText().toString() ;
                String repass = repassword.getText().toString();
                int u=(pass.replaceAll("\\s+","")).length();
                int p=(pass.replaceAll("\\s+","")).length();


                if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pass) ||TextUtils.isEmpty(repass) || u<3 || p<3) { //verifier si l'un des champs est vide
                    if(TextUtils.isEmpty(user) || u<3 ) {
                        Toast.makeText(RegisterActivity.this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show();
                        email.setError("entrer un email");
                    }
                    if(TextUtils.isEmpty(pass) || p<3 ) {
                        Toast.makeText(RegisterActivity.this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show();
                        password.setError("entrer plus que 3 cracteres");
                    }
                    if(TextUtils.isEmpty(repass)){
                        Toast.makeText(RegisterActivity.this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show();
                        repassword.setError("entrer plus que 3 cracteres");
                    }
                }
                else {
                    if (pass.equals(repass)){
                        Boolean checkuser = DB.checkusername(user);
                        if (checkuser){
                            if(isEmailValid(user)){
                                Boolean insert = DB.insertData(user, pass, image );
                                if (insert == true) {
                                    Variables.email = user;
                                    Variables.id = DB.getId(user);
                                    Toast.makeText(RegisterActivity.this, "Enregistré avec succès", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Enregistrement échoué", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(RegisterActivity.this,"Entrer un email",Toast.LENGTH_SHORT).show();
                                email.setError("Entrer un email");
                            }
                        }else{
                            Toast.makeText(RegisterActivity.this,"email deja utilisé",Toast.LENGTH_SHORT).show();
                            email.setError("email deja utilisé");
                        }
                    }else{
                        Toast.makeText(RegisterActivity.this,"le mot de passe de confirmation doit etre egal au mot de passe",Toast.LENGTH_SHORT).show();
                        repassword.setError("le mot de passe de confirmation doit etre egal au mot de passe");
                    }
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),loginActivity.class);
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
        repassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (password.getRight() - repassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(!repasswordVesibility){
                            repassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            repassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24,0,R.drawable.show_password,0);
                            repasswordVesibility=true;
                        }else {
                            repassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24,0,R.drawable.hide_password,0);
                            repassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            repasswordVesibility=false;
                        }

                        return true;
                    }
                }
                return false;
            }
        });

    }

    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}