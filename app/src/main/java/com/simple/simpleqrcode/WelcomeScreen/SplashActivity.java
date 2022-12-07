package com.simple.simpleqrcode.WelcomeScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.simple.simpleqrcode.HomeScreen.HomeActivity;
import com.simple.simpleqrcode.HomeScreen.MainHomeActivity;
import com.simple.simpleqrcode.LoginScreen.LoginActivity;
import com.simple.simpleqrcode.R;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences userDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        userDetail = this.getSharedPreferences("userDetail",0);
        Boolean Registered = userDetail.getBoolean("Registered",false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (isNetworkConnected())
                {
                    if (Registered)
                   {
                        final Intent mainIntent = new Intent(SplashActivity.this, MainHomeActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();

                    }
                    else
                    {
                        //PhoneSignInActivity
                        final Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                }
                else
                {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(SplashActivity.this);
                    builder.setMessage("Please Check Your Internet Connection")
                            .setCancelable(false)
                            .setPositiveButton("Exit", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    Log.e("tem",dialog+""+id);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }


            }
        }, 3000);
    }
    private boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}