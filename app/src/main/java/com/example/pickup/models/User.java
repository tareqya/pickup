package com.example.pickup.models;

import java.io.Serializable;

public class User extends FirebaseId implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String imagePath;
    private String imageUrl;
    private double score;

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public double getScore() {
        return score;
    }

    public User setScore(double score) {
        this.score = score;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public User setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public User setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }
}
