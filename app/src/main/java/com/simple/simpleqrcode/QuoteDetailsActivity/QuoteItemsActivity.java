package com.simple.simpleqrcode.QuoteDetailsActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simple.simpleqrcode.Adapters.QuoteAdapter;
import com.simple.simpleqrcode.Adapters.SqlQuoteAdapter;
import com.simple.simpleqrcode.Adapters.TestQuoteAdapter;
import com.simple.simpleqrcode.ExportScreen.ExportActivity;
import com.simple.simpleqrcode.Models.ProductsModel;
import com.simple.simpleqrcode.Models.QuoteModel;
import com.simple.simpleqrcode.R;
import com.simple.simpleqrcode.SQLDB.DbManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class QuoteItemsActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceQuote;
    RecyclerView recyclerView;
    QuoteAdapter adapter;
    TestQuoteAdapter testQuoteAdapter;
    TextView subTotalAmt,totalGstAmt,grandTotal,delete;
    int totalOfSubTotal;
    ProductsModel model;
    ImageView quoteBackBtn,addQuoteImg;
    MaterialButton sharedPDF;
    LinearLayout quoteLayout;
    SharedPreferences userDetail;
    TextView customerName,customerNo,date;
    String getCustomerName,getCustomerNo;
    int hours,minutes,seconds;
    List<ProductsModel> allQuoteItemList = new ArrayList<ProductsModel>();
    ArrayList<QuoteModel> dataholder = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_items);

        recyclerView = findViewById(R.id.quoteRecyclerview);
        subTotalAmt = findViewById(R.id.subTotalAmt);
        totalGstAmt = findViewById(R.id.totalGstAmt);
        grandTotal = findViewById(R.id.grandTotal);
        quoteBackBtn = findViewById(R.id.quoteBackButton);
        delete = findViewById(R.id.deleted);
        sharedPDF = findViewById(R.id.sharePDF);
        quoteLayout = findViewById(R.id.quoteDetailsLayout);
        customerNo = findViewById(R.id.customerNoQuote);
        customerName = findViewById(R.id.customerNameQuote);
        date = findViewById(R.id.dateOnQuote);
        addQuoteImg = findViewById(R.id.addQuote);

        addQuoteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userDetail = this.getSharedPreferences("userDetail",0);

        getCustomerName = userDetail.getString("customerName","name");
        getCustomerNo  = userDetail.getString("customerNo","no");

        customerName.setText(getCustomerName);
        customerNo.setText(getCustomerNo);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        date.setText(formattedDate);
        Date dt = new Date();
         hours = dt.getHours();
         minutes = dt.getMinutes();
         seconds = dt.getSeconds();
        //allQuoteItemList = (List<ProductsModel>) getIntent().getParcelableExtra("mylist");
        ArrayList<ProductsModel> myList = (ArrayList<ProductsModel>) getIntent().getSerializableExtra("mylist");

        Log.e("list", String.valueOf(myList));

        testQuoteAdapter = new TestQuoteAdapter(myList,QuoteItemsActivity.this);


        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReferenceQuote = firebaseDatabase.getReference("Quote");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<QuoteModel> options = new FirebaseRecyclerOptions.Builder<QuoteModel>()
                .setQuery(databaseReferenceQuote,QuoteModel.class).build();

        adapter = new QuoteAdapter(options,subTotalAmt,totalGstAmt,grandTotal,sharedPDF,quoteLayout);
//        recyclerView.setAdapter(testQuoteAdapter);
//        adapter.startListening();

        Cursor cursor = new DbManager(this).readalldata();

        while (cursor.moveToNext())
        {
            QuoteModel model = new QuoteModel(cursor.getString(2),cursor.getString(4),cursor.getString(3),
                    cursor.getString(5),cursor.getString(3));
            dataholder.add(model);
            Set<QuoteModel> set = new HashSet<>(dataholder);
            dataholder.clear();
            dataholder.addAll(set);
        }


        Set<QuoteModel> set=new HashSet<>(dataholder);
        List<QuoteModel> allQuoteItemList = new ArrayList<>();

        allQuoteItemList.addAll(set);

        Log.e("listvalue",allQuoteItemList.toString());

        SqlQuoteAdapter sqlQuoteAdapter = new SqlQuoteAdapter(dataholder,subTotalAmt,totalGstAmt,grandTotal);
        recyclerView.setAdapter(sqlQuoteAdapter);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(QuoteItemsActivity.this, "done", Toast.LENGTH_SHORT).show();
                DbManager dbManager = new DbManager(QuoteItemsActivity.this);
                dbManager.deleteAll();

            }
        });



        quoteBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sharedPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sqlQuoteAdapter.setVisibility();

                PdfGenerator.getBuilder()
                        .setContext(QuoteItemsActivity.this)
                        .fromViewSource()
                        .fromView(quoteLayout)
                        .setFileName("Test-PDF"+String.valueOf(hours+minutes+seconds))
                        .setFolderNameOrPath("FolderC")
                        .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
                        .build(new PdfGeneratorListener() {
                            @Override
                            public void onFailure(FailureResponse failureResponse) {
                                super.onFailure(failureResponse);
                            }

                            @Override
                            public void showLog(String log) {
                                super.showLog(log);
                            }

                            @Override
                            public void onStartPDFGeneration() {
                                /*When PDF generation begins to start*/
                            }

                            @Override
                            public void onFinishPDFGeneration() {
                                /*When PDF generation is finished*/
                                Toast.makeText(QuoteItemsActivity.this, "generated", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(SuccessResponse response) {
                                super.onSuccess(response);
                                Toast.makeText(QuoteItemsActivity.this, "Successfully saved", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
    @Override
    public void onBackPressed()
    {


        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage("Do you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DbManager dbManager = new DbManager(QuoteItemsActivity.this);
                        dbManager.deleteAll();
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();


    }

//        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Quote");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int totalOfSubTotal = 0;
//                int totalOfGst = 0;
//
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//
//                    String mrp=snapshot.child("mrp").getValue().toString();
//                    String gst=snapshot.child("agtMrp").getValue().toString();
//                    totalOfSubTotal = totalOfSubTotal + Integer.parseInt(mrp);
//                    double amount = Double.parseDouble(mrp.toString());
//                    double res = (amount / 100.0f) * Integer.parseInt(gst);
//                    Log.e("per",String.valueOf(res));
//                    Double d = new Double(res);
//                    int i = d.intValue();
//                    totalOfGst = totalOfGst + i;
//
//
//                }
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });

    }
