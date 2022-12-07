package com.simple.simpleqrcode.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simple.simpleqrcode.Models.ProductsModel;
import com.simple.simpleqrcode.Models.QuoteModel;
import com.simple.simpleqrcode.QuoteDetailsActivity.QuoteItemsActivity;
import com.simple.simpleqrcode.R;

import java.util.Date;

public class QuoteAdapter extends FirebaseRecyclerAdapter<QuoteModel,QuoteAdapter.MyViewHolder> {

    TextView subTotalAmt,totalGstAmt,grandTotal;
    MaterialButton sharedPDF;
    LinearLayout quoteLayout;
    int totalOfSubTotal = 0;
    int totalOfGst = 0;
    private boolean isVisible = true;
    public QuoteAdapter(@NonNull FirebaseRecyclerOptions<QuoteModel> options, TextView subTotalAmt, TextView totalGstAmt, TextView grandTotal, MaterialButton sharedPDF, LinearLayout quoteLayout) {
        super(options);
        this.subTotalAmt = subTotalAmt;
        this.totalGstAmt = totalGstAmt;
        this.grandTotal = grandTotal;
        this.sharedPDF = sharedPDF;
        this.quoteLayout = quoteLayout;
    }

    @Override
    protected void onBindViewHolder(@NonNull QuoteAdapter.MyViewHolder holder, int position, @NonNull QuoteModel model) {

        holder.cartItemName.setText(model.getName());
        holder.cartItemRate.setText(model.getMrp());
        holder.cartItemGst.setText(model.getAgtMrp()+"%");
        holder.CartQuantityText.setText(model.getQty());

        Date dt = new Date();
        int hours = dt.getHours();
        int minutes = dt.getMinutes();
        int seconds = dt.getSeconds();

        if(isVisible){
            holder.minusQuoteImg.setVisibility(View.VISIBLE);
        }else{
            holder.minusQuoteImg.setVisibility(View.GONE);
            PdfGenerator.getBuilder()
                    .setContext(holder.itemView.getContext())
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
                            Toast.makeText(holder.itemView.getContext(), "generated", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(SuccessResponse response) {
                            super.onSuccess(response);
                            Toast.makeText(holder.itemView.getContext(), "Successfully saved", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        totalOfSubTotal = totalOfSubTotal + (Integer.parseInt(model.getQty() )*Integer.parseInt( model.getMrp()));
        double amount = Double.parseDouble(model.getMrp().toString());
        double res = (amount / 100.0f) * Integer.parseInt(model.getAgtMrp());
        Log.e("per",String.valueOf(res));
        Double d = new Double(res);
        int i = d.intValue();
        totalOfGst = totalOfGst + i;

        subTotalAmt.setText(String.valueOf(totalOfSubTotal));
        totalGstAmt.setText(String.valueOf(totalOfGst));
        grandTotal.setText(String.valueOf(totalOfSubTotal+totalOfGst));



        sharedPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVisible = false;
            }
        });

    }

    @NonNull
    @Override
    public QuoteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_view_layout,parent,false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cartItemName,cartItemRate,cartItemGst;
        EditText CartQuantityText;
        ImageView minusQuoteImg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cartItemName = itemView.findViewById(R.id.cartItemName);
            cartItemRate = itemView.findViewById(R.id.cartItemRate);
            cartItemGst = itemView.findViewById(R.id.cartItemGst);
            CartQuantityText = itemView.findViewById(R.id.CartQuantityText);
            minusQuoteImg = itemView.findViewById(R.id.minusQuoteItem);

            CartQuantityText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

//                    if (!CartQuantityText.getText().toString().equals("") && !CartQuantityText.isFocused())
//                    {
//                        CartQuantityText.clearFocus();
//
//                    }

                }
            });

            CartQuantityText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                    if (actionId == EditorInfo.IME_ACTION_DONE)
                    {
                        InputMethodManager imm = (InputMethodManager)itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(CartQuantityText.getWindowToken(), 0);

//                        int totalAfterQtyChange = Integer.parseInt(cartItemRate.getText().toString())*Integer.parseInt(CartQuantityText.getText().toString());
//                        int previousSubTotal = Integer.parseInt(subTotalAmt.getText().toString());
//                        int totalAfterChange = previousSubTotal - Integer.parseInt(cartItemRate.getText().toString());
//                        int subTotalAfterChange = totalAfterChange + totalAfterQtyChange;
//
//
//                        Toast.makeText(itemView.getContext(), String.valueOf(subTotalAfterChange), Toast.LENGTH_SHORT).show();
//
//                        subTotalAmt.setText(String.valueOf(subTotalAfterChange));

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReferenceQuote = firebaseDatabase.getReference("Quote");

                        int position = getBindingAdapterPosition();
                        databaseReferenceQuote.child(getSnapshots().getSnapshot(position).getKey()).child("qty").setValue(CartQuantityText.getText().toString());
                        totalOfSubTotal = 0;
                        totalOfGst = 0;
                        CartQuantityText.clearFocus();
                        subTotalAmt.setText(String.valueOf(totalOfSubTotal));
                        totalGstAmt.setText(String.valueOf(totalOfGst));
                        grandTotal.setText(String.valueOf(totalOfSubTotal+totalOfGst));
                        QuoteAdapter.this.stopListening();
                        QuoteAdapter.this.startListening();
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
