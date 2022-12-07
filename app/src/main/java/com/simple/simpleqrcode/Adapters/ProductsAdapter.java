package com.simple.simpleqrcode.Adapters;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simple.simpleqrcode.ExportScreen.ExportActivity;
import com.simple.simpleqrcode.HomeScreen.HomeActivity;
import com.simple.simpleqrcode.Models.ProductsModel;
import com.simple.simpleqrcode.R;

public class ProductsAdapter extends FirebaseRecyclerAdapter<ProductsModel,ProductsAdapter.MyViewHolder> {
    String name;
    String code;
    String mrp;
    String key;
    String status;
    SharedPreferences userDetail;
    FirebaseDatabase firebaseDatabase;

    private onCardClickListner cardClickListner;
    public ProductsAdapter(@NonNull FirebaseRecyclerOptions<ProductsModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductsAdapter.MyViewHolder holder, int position, @NonNull ProductsModel model) {

        userDetail = holder.itemView.getContext().getSharedPreferences("userDetail",0);
        status = userDetail.getString("status","error");
        name = model.getName();
        code = model.getCode();
        mrp = model.getMrp();
        key = model.getKey();

        if (name.equals(""))
        {
            holder.productName.setText("N/A");
        }
        else
        {
            holder.productName.setText(model.getName());
        }
        if (code.equals(""))
        {
            holder.productCode.setText("N/A:");
        }
        else
        {
            holder.productCode.setText(model.getCode() +":");
        }
        if (mrp.equals(""))
        {
            holder.productMrp.setText("N/A");
        }
        else
        {
            if (status.equals("agent"))
            {
                holder.productMrp.setText(model.getAgtMrp());
            }
            if (status.equals("user"))
            {
                holder.productMrp.setText(model.getCstMrp());
            }
            if (status.equals("admin"))
            {
                holder.productMrp.setText(model.getMrp());
            }

        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReferenceExport = firebaseDatabase.getReference("Export");
        databaseReferenceExport.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReferenceExport.removeEventListener(this);
                if (snapshot.exists()) {
                    for(DataSnapshot item_snapshot:snapshot.getChildren()) {
                        if (item_snapshot.child("name").getValue().toString().equals(holder.productName.getText())) {
                            holder.checkImage.setVisibility(View.VISIBLE);
                            holder.productViewLayout.setBackgroundColor(Color.parseColor("#eafaff"));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public ProductsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_layout,parent,false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView productName,productCode,productMrp;
        LinearLayout productViewLayout;
        ImageView checkImage;
        FirebaseDatabase firebaseDatabase;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.listProductName);
            productCode = itemView.findViewById(R.id.listProductCode);
            productMrp = itemView.findViewById(R.id.listProductMrp);
            checkImage = itemView.findViewById(R.id.checkImg);
            productViewLayout = itemView.findViewById(R.id.productViewLayout);


            productViewLayout.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && cardClickListner != null) {
                    cardClickListner.onCardClick(getSnapshots().getSnapshot(position),position,checkImage,productViewLayout);
                }
            });

            productViewLayout.setOnHoverListener(new View.OnHoverListener() {
                @Override
                public boolean onHover(View v, MotionEvent event) {

                    Toast.makeText(itemView.getContext(), productName.getText().toString(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });


            productViewLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getBindingAdapterPosition();

                    //Toast.makeText(v.getContext(), "Long Press", Toast.LENGTH_SHORT).show();
                    checkImage.setVisibility(View.VISIBLE);
                    productViewLayout.setBackgroundColor(Color.parseColor("#eafaff"));
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReferenceExport = firebaseDatabase.getReference("Export");
                    String selectedPosition = getSnapshots().getSnapshot(position).child("key").getValue().toString();
                    databaseReferenceExport.child(selectedPosition).child("key").setValue(selectedPosition);
                    databaseReferenceExport.child(selectedPosition).child("name").setValue(productName.getText().toString());
                    Toast.makeText(v.getContext(),getSnapshots().getSnapshot(position).child("key").getValue().toString(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    public interface onCardClickListner{
        void onCardClick(DataSnapshot snapshot, int position, ImageView ImageView,LinearLayout linearLayout);
    }
    public void setOnClickListner(onCardClickListner cardClickListner){this.cardClickListner = cardClickListner;}
}
