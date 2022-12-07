package com.simple.simpleqrcode.ProductDetailsScreen;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simple.simpleqrcode.HomeScreen.HomeActivity;
import com.simple.simpleqrcode.LoginScreen.LoginActivity;
import com.simple.simpleqrcode.R;

public class AddProductActivity extends AppCompatActivity {

    EditText prodName,prodCode,prodMrp,prodAgtMrp;
    MaterialButton submitBtn,addMoreBtn;
    DatabaseReference databaseReferenceProduct;
    FirebaseDatabase firebaseDatabase;
    SharedPreferences userDetail;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        prodName = findViewById(R.id.nameProductName);
        prodCode = findViewById(R.id.codeProductCode);
        prodMrp = findViewById(R.id.mrpAddProduct);
        prodAgtMrp = findViewById(R.id.agentMrpAddProduct);
        submitBtn = findViewById(R.id.submitBtn);
        addMoreBtn = findViewById(R.id.addMoreBtn);
        backBtn = findViewById(R.id.addProductBackButton);
        userDetail = this.getSharedPreferences("userDetail",0);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceProduct = firebaseDatabase.getReference("Products");

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mProdName = prodName.getText().toString();
                String mProdCode = prodCode.getText().toString();
                String MProdMrp = prodMrp.getText().toString();
                String MProdAgtMrp = prodAgtMrp.getText().toString();

                    databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            databaseReferenceProduct.removeEventListener(this);

                            if (snapshot.exists())
                            {
                                int totalProducts = (int) snapshot.getChildrenCount() + 1;
                                String key = String.valueOf(totalProducts);
                                databaseReferenceProduct.child(key).child("name").setValue(mProdName).toString();
                                databaseReferenceProduct.child(key).child("code").setValue(mProdCode).toString();
                                databaseReferenceProduct.child(key).child("mrp").setValue(MProdMrp).toString();
                                databaseReferenceProduct.child(key).child("agtMrp").setValue(MProdAgtMrp).toString();
                                databaseReferenceProduct.child(key).child("key").setValue(key).toString();
                                Toast.makeText(AddProductActivity.this, "Successfully Added product", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                String key = String.valueOf(1);
                                databaseReferenceProduct.child(key).child("name").setValue(mProdName).toString();
                                databaseReferenceProduct.child(key).child("code").setValue(mProdCode).toString();
                                databaseReferenceProduct.child(key).child("mrp").setValue(MProdMrp).toString();
                                databaseReferenceProduct.child(key).child("agtMrp").setValue(MProdAgtMrp).toString();
                                databaseReferenceProduct.child(key).child("key").setValue(key).toString();
                                Toast.makeText(AddProductActivity.this, "Successfully Added product", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            }
        });

        addMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prodName.setText(null);
                prodCode.setText(null);
                prodMrp.setText(null);
                prodAgtMrp.setText(null);
                prodCode.setFocusable(true);
                prodCode.requestFocus();
            }
        });
    }
}