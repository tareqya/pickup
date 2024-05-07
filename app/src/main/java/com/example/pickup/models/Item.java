package com.example.pickup.models;

import java.io.Serializable;

public class Item extends FirebaseId implements Serializable {
    private String name;
    private double price;
    private String imagePath;
    private String imageUrl;

    public Item(){}

    public String getName() {
        return name;
    }

    public Item setName(String name) {
        this.name = name;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Item setPrice(double price) {
        this.price = price;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Item setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Item setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }
}
