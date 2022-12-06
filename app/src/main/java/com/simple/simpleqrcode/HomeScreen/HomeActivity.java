package com.simple.simpleqrcode.HomeScreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.simple.simpleqrcode.Adapters.ProductsAdapter;
import com.simple.simpleqrcode.Classes.Capture;
import com.simple.simpleqrcode.ExportScreen.ExportActivity;
import com.simple.simpleqrcode.Models.ProductsModel;
import com.simple.simpleqrcode.ProductDetailsScreen.AddProductActivity;
import com.simple.simpleqrcode.ProductDetailsScreen.ShowProductActivity;
import com.simple.simpleqrcode.R;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {


    SharedPreferences userDetail;
    String status;
    ImageView addProductImage,openMenuImage;
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceProducts;
    ProductsAdapter adapter;
    FloatingActionButton fabScanner;
    SearchView searchView;
    String sourceString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userDetail = this.getSharedPreferences("userDetail",0);
        status = userDetail.getString("status","error");

        addProductImage = findViewById(R.id.addProduct);
        openMenuImage = findViewById(R.id.openMenu);
        recyclerView = findViewById(R.id.recyclerView);
        fabScanner = findViewById(R.id.fabOpenScanner);
        searchView = findViewById(R.id.searchView);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceProducts = firebaseDatabase.getReference("Products");

        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

        FirebaseRecyclerOptions<ProductsModel> options = new FirebaseRecyclerOptions.Builder<ProductsModel>()
                .setQuery(databaseReferenceProducts,ProductsModel.class).build();

        adapter = new ProductsAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.setItemViewCacheSize(90);
        adapter.startListening();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                processesSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                processesSearch(newText);
                return false;
            }
        });

        openMenuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initializing the popup menu and giving the reference as current context
                PopupMenu popupMenu = new PopupMenu(HomeActivity.this, openMenuImage);

                // Inflating popup menu from popup_menu_home_screen.xml file
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_home_screen, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId())
                        {
                            case R.id.select:
                                userDetail.edit().putString("path","Export").apply();
                                Intent intent = new Intent(HomeActivity.this, ExportActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.selectall:
                                userDetail.edit().putString("path","Products").apply();
                                Intent i = new Intent(HomeActivity.this, ExportActivity.class);
                                startActivity(i);
                                return true;

                        }
                        return true;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });

        if (status.equals("admin"))
        {
        addProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });
        }
        else
        {
            Toast.makeText(this,"Access Only to Admin", Toast.LENGTH_SHORT).show();
        }


        ProductsAdapter.onCardClickListner cardClickListner = new ProductsAdapter.onCardClickListner() {
            @Override
            public void onCardClick(DataSnapshot snapshot, int position, ImageView checkImage, LinearLayout productViewLayout) {
                DatabaseReference databaseReferenceExport = firebaseDatabase.getReference("Export");


                databaseReferenceExport.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        databaseReferenceExport.removeEventListener(this);


                        if (checkImage.getVisibility()== View.VISIBLE)
                        {
                            checkImage.setVisibility(View.GONE);
                            productViewLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                            databaseReferenceExport.child(snapshot.getKey().toString()).child("key").removeValue();
                            databaseReferenceExport.child(snapshot.getKey().toString()).child("name").removeValue();
                        }
                        else
                        {
                            if (checkImage.getVisibility()== View.GONE && !dataSnapshot.exists())
                            {
                                Toast.makeText(HomeActivity.this, "dataSnapshot Not exist", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(HomeActivity.this, ShowProductActivity.class);
                                intent.putExtra("key", snapshot.getKey().toString());
                                intent.putExtra("code", snapshot.child("code").getValue().toString());
                                intent.putExtra("name", snapshot.child("name").getValue().toString());
                                intent.putExtra("mrp", snapshot.child("mrp").getValue().toString());
                                intent.putExtra("agtMrp", snapshot.child("agtMrp").getValue().toString());
                                intent.putExtra("cstMrp", snapshot.child("cstMrp").getValue().toString());
                                startActivity(intent);
                            }
                            else
                            {
                                checkImage.setVisibility(View.VISIBLE);
                                productViewLayout.setBackgroundColor(Color.parseColor("#eafaff"));
                                databaseReferenceExport.child(snapshot.getKey().toString()).child("key").setValue(snapshot.getKey().toString());
                                databaseReferenceExport.child(snapshot.getKey().toString()).child("name").setValue(snapshot.child("name").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        };

        adapter.setOnClickListner(cardClickListner);

        fabScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(HomeActivity.this, ScannerActivity.class);
//                startActivity(intent);
//                finish();
                IntentIntegrator intentIntegrator = new IntentIntegrator(HomeActivity.this);
                intentIntegrator.setPrompt("For Flash Use Volume Key up");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(Capture.class);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.initiateScan();

            }
        });


    }
    private void processesSearch(String query) {

        FirebaseRecyclerOptions<ProductsModel> options =
                new FirebaseRecyclerOptions.Builder<ProductsModel>()
                        .setQuery(databaseReferenceProducts.orderByChild("name").startAt(query.toUpperCase(Locale.ROOT)).endAt(query+"\uf8ff"),ProductsModel.class).build();
        adapter = new ProductsAdapter(options);
        adapter.startListening();
        recyclerView.setAdapter(adapter);

        ProductsAdapter.onCardClickListner cardClickListner = new ProductsAdapter.onCardClickListner() {
            @Override
            public void onCardClick(DataSnapshot snapshot, int position, ImageView checkImage, LinearLayout productViewLayout) {
                DatabaseReference databaseReferenceExport = firebaseDatabase.getReference("Export");


                databaseReferenceExport.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        databaseReferenceExport.removeEventListener(this);


                        if (checkImage.getVisibility()== View.VISIBLE)
                        {
                            checkImage.setVisibility(View.GONE);
                            productViewLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                            databaseReferenceExport.child(snapshot.getKey().toString()).child("key").removeValue();
                            databaseReferenceExport.child(snapshot.getKey().toString()).child("name").removeValue();
                        }
                        else
                        {
                            if (checkImage.getVisibility()== View.GONE && !dataSnapshot.exists())
                            {
                                Toast.makeText(HomeActivity.this, "dataSnapshot Not exist", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(HomeActivity.this, ShowProductActivity.class);
                                intent.putExtra("key", snapshot.getKey().toString());
                                intent.putExtra("code", snapshot.child("code").getValue().toString());
                                intent.putExtra("name", snapshot.child("name").getValue().toString());
                                intent.putExtra("mrp", snapshot.child("mrp").getValue().toString());
                                intent.putExtra("agtMrp", snapshot.child("agtMrp").getValue().toString());
                                intent.putExtra("cstMrp", snapshot.child("cstMrp").getValue().toString());
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(HomeActivity.this, "dataSnapshot exist", Toast.LENGTH_SHORT).show();
                                checkImage.setVisibility(View.VISIBLE);
                                productViewLayout.setBackgroundColor(Color.parseColor("#eafaff"));
                                databaseReferenceExport.child(snapshot.getKey().toString()).child("key").setValue(snapshot.getKey().toString());
                                databaseReferenceExport.child(snapshot.getKey().toString()).child("name").setValue(snapshot.child("name").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        };

        adapter.setOnClickListner(cardClickListner);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (intentResult.getContents()!=null){

            databaseReferenceProducts.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    databaseReferenceProducts.removeEventListener(this);

                    String prodName = snapshot.child(intentResult.getContents().toString()).child("name").getValue().toString();
                    String prodCode = snapshot.child(intentResult.getContents().toString()).child("code").getValue().toString();
                    String prodMrp = snapshot.child(intentResult.getContents().toString()).child("mrp").getValue().toString();
                    String prodAgtMrp = snapshot.child(intentResult.getContents().toString()).child("agtMrp").getValue().toString();
                    String prodCstMrp = snapshot.child(intentResult.getContents().toString()).child("cstMrp").getValue().toString();
                    Toast.makeText(HomeActivity.this, prodAgtMrp + prodCstMrp, Toast.LENGTH_SHORT).show();

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
                    AlertDialog.Builder  builder = new AlertDialog.Builder(HomeActivity.this,R.style.AlertDialogTheme);
                    builder.setTitle("Result");
                    builder.setMessage(Html.fromHtml(sourceString));
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            IntentIntegrator intentIntegrator = new IntentIntegrator(HomeActivity.this);
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
                            IntentIntegrator intentIntegrator = new IntentIntegrator(HomeActivity.this);
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

            Toast.makeText(this, "Oops didn't find any barcode", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchView.clearFocus();
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
                        userDetail.edit().putBoolean("showBanner",true).apply();
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();


    }
}