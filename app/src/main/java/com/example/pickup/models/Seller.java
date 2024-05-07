package com.example.pickup.models;

public class Seller extends FirebaseId{
    private String name;
    private String recycleProduct;
    private String phone;

    public Seller(){}

    public String getName() {
        return name;
    }

    public Seller setName(String name) {
        this.name = name;
        return this;
    }

    public String getRecycleProduct() {
        return recycleProduct;
    }

    public Seller setRecycleProduct(String recycleProduct) {
        this.recycleProduct = recycleProduct;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Seller setPhone(String phone) {
        this.phone = phone;
        return this;
    }
}
