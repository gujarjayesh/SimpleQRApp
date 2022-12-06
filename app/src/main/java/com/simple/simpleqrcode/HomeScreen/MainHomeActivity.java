package com.simple.simpleqrcode.HomeScreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.simple.simpleqrcode.Fragments.HomeFragment;
import com.simple.simpleqrcode.Fragments.ProductFragment;
import com.simple.simpleqrcode.Fragments.ProfileFragment;
import com.simple.simpleqrcode.R;
import com.simple.simpleqrcode.ScannerScreen.QuoteScannerActivity;
import com.simple.simpleqrcode.ScannerScreen.ScannerActivity;


public class MainHomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new HomeFragment()).commit();



        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new HomeFragment()).commit();
                        return true;

                    case R.id.product:
                        Intent intent = new Intent(MainHomeActivity.this, ScannerActivity.class);
                        startActivity(intent);
                        finish();
                        return true;

                    case R.id.profile:

//
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame,new ProfileFragment()).commit();
                        return true;

                }
                return false;
            }
        });


    }
    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage("Do you want to exit the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }


}