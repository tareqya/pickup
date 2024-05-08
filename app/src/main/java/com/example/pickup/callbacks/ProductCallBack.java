package com.example.pickup.callbacks;

import com.example.pickup.models.Product;

import java.util.ArrayList;

public interface ProductCallBack {
    void onFetchProductsComplete(ArrayList<Product> products);
}
