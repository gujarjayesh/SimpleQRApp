package com.simple.simpleqrcode.ScannerScreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.simple.simpleqrcode.Classes.Capture;
import com.simple.simpleqrcode.HomeScreen.MainHomeActivity;
import com.simple.simpleqrcode.R;

public class ScannerActivity extends AppCompatActivity {

    DatabaseReference databaseReferenceProduct;
    FirebaseDatabase firebaseDatabase;
    SharedPreferences userDetail;
    String status;
    String sourceString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        userDetail = this.getSharedPreferences("userDetail",0);
        status = userDetail.getString("status","error");

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("For Flash Use Volume Key up");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (intentResult.getContents()!=null){

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReferenceProduct = firebaseDatabase.getReference("Products");

            databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    databaseReferenceProduct.removeEventListener(this);

                    String prodName = snapshot.child(intentResult.getContents().toString()).child("name").getValue().toString();
                    String prodCode = snapshot.child(intentResult.getContents().toString()).child("code").getValue().toString();
                    String prodMrp = snapshot.child(intentResult.getContents().toString()).child("mrp").getValue().toString();
                    String prodAgtMrp = snapshot.child(intentResult.getContents().toString()).child("agtMrp").getValue().toString();
                    String prodCstMrp = snapshot.child(intentResult.getContents().toString()).child("cstMrp").getValue().toString();

                    Toast.makeText(ScannerActivity.this, prodAgtMrp + prodCstMrp, Toast.LENGTH_SHORT).show();

                    if (status.equals("admin"))
                    {
                         sourceString = "Name" + " : "+"<b>" + prodName + "</b>" + "<br/>" + "Code" +" : "+ "<b>" + prodCode + "</b>"  + "<br/>" + "Mrp" +" : "+ "<b>" + prodMrp + "</b>"
                                            + "<br/>" + "AgentMrp" +" : "+ "<b>" + prodAgtMrp + "</b>"+ "<br/>" + "CustomerMrp" +" : "+ "<b>" + prodCstMrp + "</b>";
                    }
                    if (status.equals("agent"))
                    {
                         sourceString = "Name" + " : "+"<b>" + prodName + "</b>" + "<br/>" + "Code" +" : "+ "<b>" + prodCode + "</b>"  + "<br/>" + "Mrp" +" : "+ "<b>" + prodAgtMrp + "</b>";
                    }
                    if (status.equals("user"))
                    {
                         sourceString = "Name" + " : "+"<b>" + prodName + "</b>" + "<br/>" + "Code" +" : "+ "<b>" + prodCode + "</b>"  + "<br/>" + "Mrp" +" : "+ "<b>" + prodCstMrp + "</b>";
                    }
                    AlertDialog.Builder  builder = new AlertDialog.Builder(ScannerActivity.this,R.style.AlertDialogTheme);
                    builder.setTitle("Result");
                    builder.setMessage(Html.fromHtml(sourceString));
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            IntentIntegrator intentIntegrator = new IntentIntegrator(ScannerActivity.this);
                            intentIntegrator.setPrompt("For Flash Use Volume Key up");
                            intentIntegrator.setBeepEnabled(true);
                            intentIntegrator.setOrientationLocked(true);
                            intentIntegrator.setCaptureActivity(Capture.class);
                            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                            intentIntegrator.initiateScan();
                        }
                    });
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            IntentIntegrator intentIntegrator = new IntentIntegrator(ScannerActivity.this);
                            intentIntegrator.setPrompt("For Flash Use Volume Key up");
                            intentIntegrator.setBeepEnabled(true);
                            intentIntegrator.setOrientationLocked(true);
                            intentIntegrator.setCaptureActivity(Capture.class);
                            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                            intentIntegrator.initiateScan();

                        }
                    });
                    builder.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else
        {
            onBackPressed();

            Toast.makeText(this, "Oops didn't find any barcode", Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ScannerActivity.this, MainHomeActivity.class);
        startActivity(intent);
        finish();
    }
}