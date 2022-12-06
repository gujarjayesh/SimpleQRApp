package com.simple.simpleqrcode.Adapters;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simple.simpleqrcode.Models.QuoteModel;
import com.simple.simpleqrcode.QuoteDetailsActivity.QuoteItemsActivity;
import com.simple.simpleqrcode.R;
import com.simple.simpleqrcode.SQLDB.DbManager;

import java.util.ArrayList;
import java.util.Date;

public class SqlQuoteAdapter extends RecyclerView.Adapter<SqlQuoteAdapter.myviewholder>
{
    ArrayList<QuoteModel> dataholder;
    TextView subTotalAmt, totalGstAmt,grandTotal;
    int totalOfSubTotal = 0;
    int totalOfGst = 0;
    private boolean isVisible = false;


    public SqlQuoteAdapter(ArrayList<QuoteModel> dataholder, TextView subTotalAmt, TextView totalGstAmt, TextView grandTotal) {
        this.dataholder = dataholder;
        this.subTotalAmt = subTotalAmt;
        this.totalGstAmt = totalGstAmt;
        this.grandTotal = grandTotal;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_view_layout,parent,false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {

        holder.cartItemName.setText(dataholder.get(position).getName());
        holder.cartItemGst.setText(dataholder.get(position).getAgtMrp()+"%");
        holder.cartItemRate.setText(dataholder.get(position).getMrp());
        holder.CartQuantityText.setText(dataholder.get(position).getQty());

        if(isVisible)
        {
            holder.minusQuoteImg.setVisibility(View.GONE);
        }else
        {
            holder.minusQuoteImg.setVisibility(View.VISIBLE);
            holder.minusQuoteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(holder.itemView.getContext(), R.style.AlertDialogTheme)
                            .setMessage("Do you want to remove this item?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name = holder.cartItemName.getText().toString();
                                    DbManager dbManager = new DbManager(holder.itemView.getContext());
                                    dbManager.deleteItem(name);
                                    totalOfSubTotal = 0;
                                    totalOfGst = 0;
                                    dataholder.remove(position);
                                    if(dataholder.size() == 0){
                                        subTotalAmt.setText(String.valueOf(0));
                                        totalGstAmt.setText(String.valueOf(0));
                                        grandTotal.setText(String.valueOf(0));
                                    }
                                    SqlQuoteAdapter.this.notifyDataSetChanged();
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();

                }
            });
        }


        totalOfSubTotal = totalOfSubTotal + (Integer.parseInt(dataholder.get(position).getQty() )*Integer.parseInt( dataholder.get(position).getMrp()));
        double amount = Double.parseDouble(String.valueOf(Integer.parseInt(dataholder.get(position).getQty() )*Integer.parseInt(dataholder.get(position).getMrp())));
        double res = (amount / 100.0f) * Integer.parseInt(dataholder.get(position).getAgtMrp());
        Log.e("per",String.valueOf(res));
        Double d = new Double(res);
        int i = d.intValue();
        totalOfGst = totalOfGst + i;

        subTotalAmt.setText(String.valueOf(totalOfSubTotal));
        totalGstAmt.setText(String.valueOf(totalOfGst));
        grandTotal.setText(String.valueOf(totalOfSubTotal+totalOfGst));

    }
    public void setVisibility(){
        isVisible = true;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    class myviewholder extends RecyclerView.ViewHolder
    {
        TextView cartItemName,cartItemRate,cartItemGst;
        EditText CartQuantityText;
        CardView cardView;
        ImageView minusQuoteImg;
        public myviewholder(@NonNull View itemView) {
            super(itemView);

            cartItemName = itemView.findViewById(R.id.cartItemName);
            cartItemRate = itemView.findViewById(R.id.cartItemRate);
            cartItemGst = itemView.findViewById(R.id.cartItemGst);
            cartItemGst = itemView.findViewById(R.id.cartItemGst);
            CartQuantityText = itemView.findViewById(R.id.CartQuantityText);
            cardView = itemView.findViewById(R.id.cardViewQuoteItems);
            minusQuoteImg = itemView.findViewById(R.id.minusQuoteItem);


//            cardView.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    int position = getBindingAdapterPosition();
//                    DbManager dbManager = new DbManager(itemView.getContext());
//                    dbManager.deleteItem(cartItemName.getText().toString());
//                    SqlQuoteAdapter.this.notifyDataSetChanged();
//                }
//            });

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

                        if (CartQuantityText.getText().toString().equals(""))
                        {
                            CartQuantityText.setText("1");
                        }
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReferenceQuote = firebaseDatabase.getReference("Quote");

                        int position = getBindingAdapterPosition();
                        dataholder.get(position).setQty(CartQuantityText.getText().toString());
                        //databaseReferenceQuote.child(getSnapshots().getSnapshot(position).getKey()).child("qty").setValue(CartQuantityText.getText().toString());
                        totalOfSubTotal = 0;
                        totalOfGst = 0;
                        CartQuantityText.clearFocus();
                        SqlQuoteAdapter.this.notifyDataSetChanged();
                        return true;
                    }
                    return false;
                }
            });

        }
    }
}
