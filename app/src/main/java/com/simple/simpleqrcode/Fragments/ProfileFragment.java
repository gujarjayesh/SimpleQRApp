package com.simple.simpleqrcode.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
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

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simple.simpleqrcode.R;
import com.simple.simpleqrcode.ScannerScreen.QuoteScannerActivity;

import java.util.Locale;


public class ProfileFragment extends Fragment {
    MaterialButton createQuoteBtn;
    SharedPreferences userDetail;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        createQuoteBtn = view.findViewById(R.id.quoteBtn);

        userDetail = getActivity().getSharedPreferences("userDetail",0);


        createQuoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog cookingSheetDialog = new BottomSheetDialog(getContext(),R.style.CartDialog);
                cookingSheetDialog.setContentView(R.layout.customer_details_layout);
                cookingSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                cookingSheetDialog.getBehavior().setHideable(true);
                cookingSheetDialog.getBehavior().setSkipCollapsed(true);
                cookingSheetDialog.setCanceledOnTouchOutside(true);

                cookingSheetDialog.show();

                TextInputEditText customerName = cookingSheetDialog.findViewById(R.id.customerNameSheetEditText);
                TextInputEditText customerNo = cookingSheetDialog.findViewById(R.id.customerNoSheetEditText);
                CardView proceedBtn = cookingSheetDialog.findViewById(R.id.proceedButton);

                proceedBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!customerName.getText().toString().equals("") && !customerNo.getText().toString().equals(""))
                        {
                            userDetail.edit().putString("customerName",customerName.getText().toString()).apply();
                            userDetail.edit().putString("customerNo",customerNo.getText().toString()).apply();

                            Intent intent1 = new Intent(getActivity(), QuoteScannerActivity.class);
                            getActivity().startActivity(intent1);
                        }
                        else
                            Toast.makeText(getContext(), "Please Provide Details", Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){


                    HomeFragment homeFragment = new HomeFragment();
//                    BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigationMenu);
//                    bottomNavigationView.show(1,true);
                    getParentFragmentManager().beginTransaction().replace(R.id.frame,homeFragment).commit();
                    return true;
                }
                return false;
            }
        });

    }
}