package com.example.pickup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pickup.R;
import com.example.pickup.callbacks.ProductListener;
import com.example.pickup.models.Product;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList<Product> products;
    private ProductListener productListener;

    public ProductAdapter(Context context, ArrayList<Product> products){
        this.context = context;
        this.products = products;
    }

    public void setProductListener(ProductListener productListener){
        this.productListener = productListener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductViewHolder productViewHolder = (ProductViewHolder) holder;
        Product product = getItem(position);

        productViewHolder.product_TV_name.setText(product.getName());
        productViewHolder.product_TV_price.setText(product.getPrice() + "");
        if(product.getImageUrl() != null)
            Glide.with(context).load(product.getImageUrl())
                    .into(productViewHolder.product_IV_productImage);
     }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public Product getItem(int pos){
        return this.products.get(pos);
    }

    public class ProductViewHolder extends  RecyclerView.ViewHolder {
        public ImageView product_IV_productImage;
        public TextView product_TV_name;
        public TextView product_TV_price;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            product_IV_productImage = itemView.findViewById(R.id.product_IV_productImage);
            product_TV_name = itemView.findViewById(R.id.product_TV_name);
            product_TV_price = itemView.findViewById(R.id.product_TV_price);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Product product = getItem(pos);
                    productListener.onClick(product);
                }
            });
        }
    }
}
