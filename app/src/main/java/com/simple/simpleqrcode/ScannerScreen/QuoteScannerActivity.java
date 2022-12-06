package com.simple.simpleqrcode.ScannerScreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.simple.simpleqrcode.Classes.Capture;
import com.simple.simpleqrcode.Models.ProductsModel;
import com.simple.simpleqrcode.QuoteDetailsActivity.QuoteItemsActivity;
import com.simple.simpleqrcode.R;
import com.simple.simpleqrcode.SQLDB.DbManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuoteScannerActivity extends AppCompatActivity {
    DatabaseReference databaseReferenceProduct;
    DatabaseReference databaseReferenceQuote;
    FirebaseDatabase firebaseDatabase;
    SharedPreferences userDetail;
    String status;
    MaterialButton proceedBtn;
    private CodeScanner mCodeScanner;
    String sourceString;
    ArrayList<String> youNameArray = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_scanner);

        proceedBtn = findViewById(R.id.proceedBtn);

        userDetail = this.getSharedPreferences("userDetail",0);
        status = userDetail.getString("status","error");

//        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
//        intentIntegrator.setPrompt("For Flash Use Volume Key up");
//        intentIntegrator.setBeepEnabled(true);
//        intentIntegrator.setOrientationLocked(true);
//        intentIntegrator.setCaptureActivity(Capture.class);
//        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
//        intentIntegrator.initiateScan();

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCodeScanner.startPreview();
                        ScanAndMakeQuote(result.getText());
                        DatabaseReference RedLineRouteReference = FirebaseDatabase.getInstance().getReference().child("Products").child(result.getText());
                        RedLineRouteReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String data = dataSnapshot.getValue().toString();
                                youNameArray.add(data);
                                processInsert( dataSnapshot.child("name").getValue().toString(),dataSnapshot.child("code").getValue().toString(),
                                        dataSnapshot.child("mrp").getValue().toString(),dataSnapshot.child("agtMrp").getValue().toString());

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });
        mCodeScanner.startPreview();

//        scannerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mCodeScanner.startPreview();
//            }
//        });

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Set<String> set=new HashSet<>(youNameArray);
                List<String> allQuoteItemList = new ArrayList<>();

                allQuoteItemList.addAll(set);
                proceedBtn.setEnabled(false);
                Intent intent = new Intent(QuoteScannerActivity.this, QuoteItemsActivity.class);
                intent.putExtra("mylist", (Serializable) allQuoteItemList);
                startActivity(intent);
            }
        });
    }

    private void processInsert(String name, String code, String mrp, String agtMrp)
    {

        String res = new DbManager(this).createQuote(code,name,agtMrp,"1",mrp);
        if (!res.equals("0"))
        {
            Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
        }
        MediaPlayer music = MediaPlayer.create(QuoteScannerActivity.this, R.raw.beep_sound);
        music.start();
        proceedBtn.setEnabled(true);
    }

    private void ScanAndMakeQuote(String key) {

//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReferenceProduct = firebaseDatabase.getReference("Products");
//        databaseReferenceQuote = firebaseDatabase.getReference("Quote");
//
//        proceedBtn.setEnabled(true);
//
//            databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    databaseReferenceProduct.removeEventListener(this);
//
//                    String prodName = snapshot.child(key).child("name").getValue().toString();
//                    String prodCode = snapshot.child(key).child("code").getValue().toString();
//                    String prodMrp = snapshot.child(key).child("mrp").getValue().toString();
//                    String prodAgtMrp = snapshot.child(key).child("agtMrp").getValue().toString();
//                    String prodCstMrp = snapshot.child(key).child("cstMrp").getValue().toString();
//
//                    databaseReferenceQuote.child(prodCode).child("name").setValue(prodName).toString();
//                    databaseReferenceQuote.child(prodCode).child("code").setValue(prodCode).toString();
//                    databaseReferenceQuote.child(prodCode).child("mrp").setValue(prodMrp).toString();
//                    databaseReferenceQuote.child(prodCode).child("agtMrp").setValue(prodAgtMrp).toString();
//                    databaseReferenceQuote.child(prodCode).child("qty").setValue("1").toString();
//
//                    Toast.makeText(QuoteScannerActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
//        if (intentResult.getContents()!=null){
//
//            firebaseDatabase = FirebaseDatabase.getInstance();
//            databaseReferenceProduct = firebaseDatabase.getReference("Products");
//
//            databaseReferenceProduct.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    databaseReferenceProduct.removeEventListener(this);
//
//                    String prodName = snapshot.child(intentResult.getContents().toString()).child("name").getValue().toString();
//                    String prodCode = snapshot.child(intentResult.getContents().toString()).child("code").getValue().toString();
//                    String prodMrp = snapshot.child(intentResult.getContents().toString()).child("mrp").getValue().toString();
//                    String prodAgtMrp = snapshot.child(intentResult.getContents().toString()).child("agtMrp").getValue().toString();
//                    String prodCstMrp = snapshot.child(intentResult.getContents().toString()).child("cstMrp").getValue().toString();
//
//                    Toast.makeText(QuoteScannerActivity.this, prodAgtMrp + prodCstMrp, Toast.LENGTH_SHORT).show();
//
//                    if (status.equals("admin"))
//                    {
//                        sourceString = "Name" + " : "+"<b>" + prodName + "</b>" + "<br/>" + "Code" +" : "+ "<b>" + prodCode + "</b>"  + "<br/>" + "Mrp" +" : "+ "<b>" + prodMrp + "</b>"
//                                + "<br/>" + "AgentMrp" +" : "+ "<b>" + prodAgtMrp + "</b>"+ "<br/>" + "CustomerMrp" +" : "+ "<b>" + prodCstMrp + "</b>";
//                    }
//                    if (status.equals("agent"))
//                    {
//                        sourceString = "Name" + " : "+"<b>" + prodName + "</b>" + "<br/>" + "Code" +" : "+ "<b>" + prodCode + "</b>"  + "<br/>" + "Mrp" +" : "+ "<b>" + prodAgtMrp + "</b>";
//                    }
//                    if (status.equals("user"))
//                    {
//                        sourceString = "Name" + " : "+"<b>" + prodName + "</b>" + "<br/>" + "Code" +" : "+ "<b>" + prodCode + "</b>"  + "<br/>" + "Mrp" +" : "+ "<b>" + prodCstMrp + "</b>";
//                    }
//                    AlertDialog.Builder  builder = new AlertDialog.Builder(QuoteScannerActivity.this,R.style.AlertDialogTheme);
//                    builder.setTitle("Result");
//                    builder.setMessage(Html.fromHtml(sourceString));
//                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            IntentIntegrator intentIntegrator = new IntentIntegrator(QuoteScannerActivity.this);
//                            intentIntegrator.setPrompt("For Flash Use Volume Key up");
//                            intentIntegrator.setBeepEnabled(true);
//                            intentIntegrator.setOrientationLocked(true);
//                            intentIntegrator.setCaptureActivity(Capture.class);
//                            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
//                            intentIntegrator.initiateScan();
//                        }
//                    });
//                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            IntentIntegrator intentIntegrator = new IntentIntegrator(QuoteScannerActivity.this);
//                            intentIntegrator.setPrompt("For Flash Use Volume Key up");
//                            intentIntegrator.setBeepEnabled(true);
//                            intentIntegrator.setOrientationLocked(true);
//                            intentIntegrator.setCaptureActivity(Capture.class);
//                            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
//                            intentIntegrator.initiateScan();
//
//                        }
//                    });
//                    builder.show();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//        }
//        else
//        {
//
//            Toast.makeText(this, "Oops didn't find any barcode", Toast.LENGTH_SHORT).show();
//
//        }
//    }
}