package com.simple.simpleqrcode.Adapters;

import static com.google.firebase.crashlytics.internal.Logger.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simple.simpleqrcode.Models.ProductsModel;
import com.simple.simpleqrcode.QuoteDetailsActivity.QuoteItemsActivity;
import com.simple.simpleqrcode.R;

import java.util.ArrayList;
import java.util.List;

public class TestQuoteAdapter extends RecyclerView.Adapter<TestQuoteAdapter.MyViewHolder> {


    private final ArrayList<ProductsModel> myList;
    QuoteItemsActivity activity;

    public TestQuoteAdapter(ArrayList<ProductsModel> myList, QuoteItemsActivity quoteItemsActivity) {
        this.myList = myList;
        this.activity = quoteItemsActivity;
    }


    @NonNull
    @Override
    public TestQuoteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_view_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestQuoteAdapter.MyViewHolder holder, int position) {

//        ProductsModel productsModel = myList.get(position);

//        String name = productsModel.getName();
//        holder.cartItemName.setText(name);

//        ProductsModel model = myList.get(position);
//
//        Log.e("zxcvbnm", String.valueOf(model));
//        String animal = String.valueOf(myList.get(position).getName());
//        holder.cartItemName.setText(animal);

//        holder.cartItemName.setText(myList.get(position).getName());
//        holder.cartItemRate.setText(myList.get(position).getMrp());
//        holder.cartItemGst.setText(myList.get(position).getAgtMrp()+"%");
        holder.CartQuantityText.setText("1");
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cartItemName,cartItemRate,cartItemGst;
        EditText CartQuantityText;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cartItemName = itemView.findViewById(R.id.cartItemName);
            cartItemRate = itemView.findViewById(R.id.cartItemRate);
            cartItemGst = itemView.findViewById(R.id.cartItemGst);
            CartQuantityText = itemView.findViewById(R.id.CartQuantityText);
        }
    }
}
