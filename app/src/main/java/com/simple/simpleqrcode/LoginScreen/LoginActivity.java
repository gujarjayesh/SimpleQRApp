package com.simple.simpleqrcode.LoginScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simple.simpleqrcode.HomeScreen.HomeActivity;
import com.simple.simpleqrcode.HomeScreen.MainHomeActivity;
import com.simple.simpleqrcode.R;

public class LoginActivity extends AppCompatActivity {

    EditText loginNo,password;
    MaterialButton loginBtn;
    DatabaseReference databaseReferenceLogin;
    FirebaseDatabase firebaseDatabase;
    SharedPreferences userDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginNo = findViewById(R.id.loginMobileNO);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        userDetail = this.getSharedPreferences("userDetail",0);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceLogin = firebaseDatabase.getReference("login");

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mPhoneNo = loginNo.getText().toString();
                String mPassword = password.getText().toString();

                databaseReferenceLogin.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReferenceLogin.removeEventListener(this);
                        if (snapshot.child(mPhoneNo).exists())
                        {
                           // Toast.makeText(LoginActivity.this, snapshot.child(mPhoneNo).child("password").getValue().toString(), Toast.LENGTH_SHORT).show();
                            if (snapshot.child(mPhoneNo).child("password").getValue().toString().equals(mPassword))
                            {
                                String status = snapshot.child(mPhoneNo).child("status").getValue().toString();
                                userDetail.edit().putString("status",status).apply();
                                userDetail.edit().putBoolean("Registered",true).apply();
                                Intent intent = new Intent(LoginActivity.this, MainHomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "wrong password", Toast.LENGTH_SHORT).show();

                            }

                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "no user Found", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        loginNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence str, int start, int count, int after)    {

            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable str1) {
                if(str1.toString().trim().length()>0 && !password.getText().toString().equals("")){
                    loginBtn.setBackgroundColor(Color.parseColor("#04B8E2"));
                    loginBtn.setEnabled(true);
                }else{
                    loginBtn.setBackgroundColor(Color.parseColor("#ABE7F5"));
                    loginBtn.setEnabled(false);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence str, int start, int count, int after)    {

            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable str) {
                if(str.toString().trim().length()>0 && !loginNo.getText().toString().equals("")){
                    loginBtn.setBackgroundColor(Color.parseColor("#04B8E2"));
                    loginBtn.setEnabled(true);

                }else{
                    loginBtn.setBackgroundColor(Color.parseColor("#ABE7F5"));
                    loginBtn.setEnabled(false);
                }
            }
        });


    }
}