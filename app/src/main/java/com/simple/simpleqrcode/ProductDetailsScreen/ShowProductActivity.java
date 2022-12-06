package com.simple.simpleqrcode.ProductDetailsScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.simple.simpleqrcode.HomeScreen.HomeActivity;
import com.simple.simpleqrcode.HomeScreen.MainHomeActivity;
import com.simple.simpleqrcode.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShowProductActivity extends AppCompatActivity {
    ImageView barcodeImage,backBtn;
    EditText prodCode,prodName,prodMrp,prodAgtMrp,prodCstMrp;
    TextView agentTv,customerTv,mrpTv;
    String prodCodeStr,prodNameStr,prodMrpStr,prodAgtStr,prodCstMrpStr,key;
    MaterialButton exportQRCodeBtn,submitBtn;
    DatabaseReference databaseReferenceProduct;
    FirebaseDatabase firebaseDatabase;
    SharedPreferences userDetail;
    String status;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product);

        barcodeImage = findViewById(R.id.barcodeImg);
        prodCode = findViewById(R.id.showProductCode);
        prodName = findViewById(R.id.showProductName);
        prodAgtMrp = findViewById(R.id.agtMrpShowProduct);
        prodCstMrp = findViewById(R.id.cstMrpShowProduct);
        prodMrp = findViewById(R.id.mrpShowProduct);
        agentTv = findViewById(R.id.tvAgentMrp);
        customerTv = findViewById(R.id.tvCustomerMrp);
        backBtn = findViewById(R.id.productBackButton);
        mrpTv = findViewById(R.id.tvMrp);
        exportQRCodeBtn = findViewById(R.id.ExportBtn);
        submitBtn = findViewById(R.id.submitShowBtn);
        userDetail = this.getSharedPreferences("userDetail",0);
        status = userDetail.getString("status","error");


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowProductActivity.this, MainHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceProduct = firebaseDatabase.getReference("Products");

        if (!status.equals("admin"))
        {
            submitBtn.setEnabled(false);
            prodMrp.setVisibility(View.GONE);
            mrpTv.setVisibility(View.GONE);

            if (status.equals("user"))
            {
                prodCode.setEnabled(false);
                prodName.setEnabled(false);
                prodAgtMrp.setVisibility(View.GONE);
                agentTv.setVisibility(View.GONE);
                prodCstMrp.setEnabled(false);
            }
            if (status.equals("agent"))
            {
                prodCode.setEnabled(false);
                prodName.setEnabled(false);
                customerTv.setVisibility(View.GONE);
                prodCstMrp.setVisibility(View.GONE);
                prodAgtMrp.setEnabled(false);
            }

        }


        exportQRCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeImage.buildDrawingCache();

                Bitmap bmp = barcodeImage.getDrawingCache();

                File storageLoc = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //context.getExternalFilesDir(null);

                File file = new File(storageLoc, prodName.getText().toString() + ".jpg");

                try{
                    FileOutputStream fos = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();

                    Toast.makeText(ShowProductActivity.this, "Exported", Toast.LENGTH_SHORT).show();

                    scanFile(ShowProductActivity.this, Uri.fromFile(file));

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Intent intent = getIntent();
        prodCodeStr = intent.getStringExtra("code");
        prodNameStr = intent.getStringExtra("name");
        prodMrpStr = intent.getStringExtra("mrp");
        prodAgtStr = intent.getStringExtra("agtMrp");
        prodCstMrpStr = intent.getStringExtra("cstMrp");
        key = intent.getStringExtra("key");

        prodCode.setText(prodCodeStr);
        prodName.setText(prodNameStr);
        prodMrp.setText(prodMrpStr);
        prodAgtMrp.setText(prodAgtStr);
        prodCstMrp.setText(prodCstMrpStr);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        BitMatrix matrix = null;
        try {
            matrix = multiFormatWriter.encode(key, BarcodeFormat.CODE_39,500,100);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        BarcodeEncoder encoder = new BarcodeEncoder();
        Bitmap bitmap = encoder.createBitmap(matrix);
        barcodeImage.setImageBitmap(bitmap);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mProdName = prodName.getText().toString();
                String mProdCode = prodCode.getText().toString();
                String MProdMrp = prodMrp.getText().toString();
                String MProdAgtMrp = prodAgtMrp.getText().toString();
                String MProdCstMrp = prodCstMrp.getText().toString();

                databaseReferenceProduct.child(key).child("name").setValue(mProdName).toString();
                databaseReferenceProduct.child(key).child("code").setValue(mProdCode).toString();
                databaseReferenceProduct.child(key).child("mrp").setValue(MProdMrp).toString();
                databaseReferenceProduct.child(key).child("agtMrp").setValue(MProdAgtMrp).toString();
                databaseReferenceProduct.child(key).child("cstMrp").setValue(MProdCstMrp).toString();

                Toast.makeText(ShowProductActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private static void scanFile(Context context, Uri imageUri){
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);

    }
}