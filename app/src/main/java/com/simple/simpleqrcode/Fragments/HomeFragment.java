package com.simple.simpleqrcode.Fragments;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simple.simpleqrcode.Adapters.ProductsAdapter;
import com.simple.simpleqrcode.ExportScreen.ExportActivity;
import com.simple.simpleqrcode.HomeScreen.HomeActivity;
import com.simple.simpleqrcode.Models.ProductsModel;
import com.simple.simpleqrcode.ProductDetailsScreen.AddProductActivity;
import com.simple.simpleqrcode.ProductDetailsScreen.ShowProductActivity;
import com.simple.simpleqrcode.R;

import java.util.Locale;

public class HomeFragment extends Fragment {
    SharedPreferences userDetail;
    String status;
    ImageView addProductImage,openMenuImage;
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceProducts;
    ProductsAdapter adapter;
    SearchView searchView;
    String sourceString;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        userDetail = getActivity().getSharedPreferences("userDetail",0);
        status = userDetail.getString("status","error");

        addProductImage = view.findViewById(R.id.addProduct);
        openMenuImage = view.findViewById(R.id.openMenu);
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceProducts = firebaseDatabase.getReference("Products");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
                PopupMenu popupMenu = new PopupMenu(getContext(), openMenuImage);

                // Inflating popup menu from popup_menu_home_screen.xml file
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu_home_screen, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId())
                        {
                            case R.id.select:
                                userDetail.edit().putString("path","Export").apply();
                                Intent intent = new Intent(getContext(), ExportActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.selectall:
                                userDetail.edit().putString("path","Products").apply();
                                Intent i = new Intent(getContext(), ExportActivity.class);
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
                    Intent intent = new Intent(getContext(), AddProductActivity.class);
                    startActivity(intent);
                }
            });
        }
        else
        {
            Toast.makeText(getContext(),"Access Only to Admin", Toast.LENGTH_SHORT).show();
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

                                Intent intent = new Intent(getContext(), ShowProductActivity.class);
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


        return view;

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
                                Toast.makeText(getContext(), "dataSnapshot Not exist", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getContext(), ShowProductActivity.class);
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
                                Toast.makeText(getContext(), "dataSnapshot exist", Toast.LENGTH_SHORT).show();
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
    public void onResume() {
        super.onResume();
        searchView.clearFocus();
    }

}