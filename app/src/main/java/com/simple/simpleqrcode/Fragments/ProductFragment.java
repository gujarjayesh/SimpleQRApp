package com.simple.simpleqrcode.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.simple.simpleqrcode.Classes.Capture;
import com.simple.simpleqrcode.HomeScreen.HomeActivity;
import com.simple.simpleqrcode.R;

import java.util.Locale;


public class ProductFragment extends Fragment {
    DatabaseReference databaseReferenceProducts;
    String sourceString;
    String status;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
        intentIntegrator.setPrompt("For Flash Use Volume Key up");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(Capture.class);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.initiateScan();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                    Toast.makeText(getContext(), prodAgtMrp + prodCstMrp, Toast.LENGTH_SHORT).show();

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
                    AlertDialog.Builder  builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogTheme);
                    builder.setTitle("Result");
                    builder.setMessage(Html.fromHtml(sourceString));
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
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
                            IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
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

            Toast.makeText(getContext(), "Oops didn't find any barcode", Toast.LENGTH_SHORT).show();

        }
    }


}