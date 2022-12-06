package com.simple.simpleqrcode.ExportScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simple.simpleqrcode.Adapters.ExportAdapter;
import com.simple.simpleqrcode.Adapters.ProductsAdapter;
import com.simple.simpleqrcode.HomeScreen.HomeActivity;
import com.simple.simpleqrcode.Models.ExportModel;
import com.simple.simpleqrcode.ProductDetailsScreen.ShowProductActivity;
import com.simple.simpleqrcode.R;

public class ExportActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceExport;
    RecyclerView recyclerView;
    ExportAdapter adapter;
    LinearLayout ExportLinearLayout;
    SharedPreferences userDetail;
    String path;
    ImageView backBtn;

    ExtendedFloatingActionButton exportPDF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        recyclerView = findViewById(R.id.exportRecyclerview);
        exportPDF = findViewById(R.id.fabExportAll);
        ExportLinearLayout = findViewById(R.id.exportLinearLayout);
        backBtn = findViewById(R.id.exportBackButton);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExportActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        userDetail = this.getSharedPreferences("userDetail",0);

        path = userDetail.getString("path","error");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceExport = firebaseDatabase.getReference(path);

        recyclerView.setLayoutManager(new LinearLayoutManager(ExportActivity.this));

        FirebaseRecyclerOptions<ExportModel> options  = new FirebaseRecyclerOptions.Builder<ExportModel>()
                .setQuery(databaseReferenceExport,ExportModel.class).build();

        adapter = new ExportAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.setItemViewCacheSize(90);
        adapter.startListening();

        exportPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PdfGenerator.getBuilder()
                        .setContext(ExportActivity.this)
                        .fromViewSource()
                        .fromView(ExportLinearLayout)
                        .setFileName("Test-PDF")
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
                                Toast.makeText(ExportActivity.this, "generated", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(SuccessResponse response) {
                                super.onSuccess(response);
                                Toast.makeText(ExportActivity.this, "Successfully saved", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


    }
}