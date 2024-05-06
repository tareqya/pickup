package com.example.pickup.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class FirebaseId implements Serializable {
    private String id;

    public FirebaseId(){}
    @Exclude
    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
}
