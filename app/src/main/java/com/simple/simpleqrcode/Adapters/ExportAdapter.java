package com.simple.simpleqrcode.Adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.simple.simpleqrcode.Models.ExportModel;
import com.simple.simpleqrcode.R;

public class ExportAdapter extends FirebaseRecyclerAdapter<ExportModel,ExportAdapter.MyViewHolder> {
    String name;
    String keyToBarcode;
    public ExportAdapter(@NonNull FirebaseRecyclerOptions<ExportModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ExportAdapter.MyViewHolder holder, int position, @NonNull ExportModel model) {
        holder.prodName.setText(model.getName());
        keyToBarcode = model.getKey().toString();

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        BitMatrix matrix = null;
        try {
            matrix = multiFormatWriter.encode(keyToBarcode, BarcodeFormat.CODE_39,500,100);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        BarcodeEncoder encoder = new BarcodeEncoder();
        Bitmap bitmap = encoder.createBitmap(matrix);
        holder.barcodeImage.setImageBitmap(bitmap);
    }

    @NonNull
    @Override
    public ExportAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.export_all_data_layout,parent,false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView prodName;
        ImageView barcodeImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            prodName = itemView.findViewById(R.id.exportProductName);
            barcodeImage = itemView.findViewById(R.id.exportBarcodeImg);



        }
    }
}
