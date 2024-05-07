package com.example.pickup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickup.R;
import com.example.pickup.models.Seller;

import java.util.ArrayList;

public class SellerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList<Seller> sellers;

    public SellerAdapter(Context context, ArrayList<Seller> sellers){
        this.context = context;
        this.sellers = sellers;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller, parent, false);
        return new SellerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SellerViewHolder sellerViewHolder = (SellerViewHolder)  holder;
        Seller seller = getItem(position);

        sellerViewHolder.seller_TV_sellerName.setText(seller.getName());
        sellerViewHolder.seller_TV_recycleProduct.setText(seller.getRecycleProduct());

    }

    @Override
    public int getItemCount() {
        return this.sellers.size();
    }

    public Seller getItem(int pos){
        return this.sellers.get(pos);
    }

    public class SellerViewHolder extends  RecyclerView.ViewHolder {
        public TextView seller_TV_sellerName;
        public TextView seller_TV_recycleProduct;
        public Button seller_BTN_call;

        public SellerViewHolder(@NonNull View itemView) {
            super(itemView);
            seller_BTN_call = itemView.findViewById(R.id.seller_BTN_call);
            seller_TV_recycleProduct = itemView.findViewById(R.id.seller_TV_recycleProduct);
            seller_TV_sellerName = itemView.findViewById(R.id.seller_TV_sellerName);

            seller_BTN_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Seller seller = getItem(pos);
                    String phone = seller.getPhone();

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    // Set the data (phone number) for the Intent
                    intent.setData(Uri.parse("tel:" + phone));
                    context.startActivity(intent);
                }
            });
        }
    }
}
