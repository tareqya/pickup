package com.example.pickup.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pickup.R;
import com.example.pickup.adapters.ProductAdapter;
import com.example.pickup.callbacks.ProductCallBack;
import com.example.pickup.callbacks.ProductListener;
import com.example.pickup.models.Product;
import com.example.pickup.models.User;
import com.example.pickup.utils.DatabaseController;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;


public class StoreFragment extends Fragment {
    private RecyclerView fStore_RV_products;
    private CircularProgressIndicator fStore_PB_loading;
    private Context context;
    private DatabaseController databaseController;
    private User currentUser;

    public StoreFragment(Context context) {
        this.context = context;
    }

    public void setCurrentUser(User currentUser){
        this.currentUser = currentUser;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        findViews(view);
        initVars();
        return view;
    }

    private void findViews(View view) {
        fStore_RV_products = view.findViewById(R.id.fStore_RV_products);
        fStore_PB_loading = view.findViewById(R.id.fStore_PB_loading);
    }

    private void initVars() {
        fStore_PB_loading.setVisibility(View.VISIBLE);
        databaseController = new DatabaseController();
        databaseController.setProductCallBack(new ProductCallBack() {
            @Override
            public void onFetchProductsComplete(ArrayList<Product> products) {
                fStore_PB_loading.setVisibility(View.INVISIBLE);
                ProductAdapter productAdapter = new ProductAdapter(context, products);
                productAdapter.setProductListener(new ProductListener() {
                    @Override
                    public void onClick(Product product) {
                        Intent intent = new Intent(context, ProductActivity.class);
                        intent.putExtra("PRODUCT", product);
                        intent.putExtra("USER", currentUser);
                        startActivity(intent);


                    }
                });

                fStore_RV_products.setLayoutManager(new GridLayoutManager(context, 2));
                fStore_RV_products.setHasFixedSize(true);
                fStore_RV_products.setItemAnimator(new DefaultItemAnimator());
                fStore_RV_products.setAdapter(productAdapter);

            }
        });

        databaseController.fetchAllProducts();
    }
}