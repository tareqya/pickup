package com.example.pickup.callbacks;

import com.example.pickup.models.Seller;

import java.util.ArrayList;

public interface SellerCallBack {
    void onFetchSellersComplete(ArrayList<Seller> sellers);
}
