package com.example.pickup.utils;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pickup.callbacks.AuthCallBack;
import com.example.pickup.callbacks.SellerCallBack;
import com.example.pickup.callbacks.UserCallBack;
import com.example.pickup.models.Seller;
import com.example.pickup.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class DatabaseController {
    public static String USERS_TABLE = "Users";
    public static String SELLERS_TABLE = "Sellers";
    private FirebaseAuth auth;
    private FirebaseFirestore mDatabase;
    private FirebaseStorage storage;

    private AuthCallBack authCallBack;
    private UserCallBack userCallBack;
    private SellerCallBack sellerCallBack;

    public DatabaseController(){
        this.auth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    public void setAuthCallBack(AuthCallBack authCallBack){
        this.authCallBack = authCallBack;
    }
    public void setUserCallBack(UserCallBack userCallBack){
        this.userCallBack = userCallBack;
    }
    public void setSellersCallBack(SellerCallBack sellerCallBack){
        this.sellerCallBack = sellerCallBack;
    }
    public void loginUser(String email, String password){

        this.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        authCallBack.onLoginComplete(task);
                    }
                });
    }

    public void createAuthUser(String email, String password){
        this.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        authCallBack.onCreateAuthUserComplete(task);
                    }
                });
    }

    public FirebaseUser currentUser(){
        return this.auth.getCurrentUser();
    }

    public void saveUser(User user){
        this.mDatabase.collection(USERS_TABLE).document(user.getId()).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                userCallBack.onSaveUserComplete(task);
            }
        });
    }

    public void fetchUser(String uid){
        this.mDatabase.collection(USERS_TABLE).document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value == null) return;

                User user = value.toObject(User.class);
                if(user.getImagePath() != null){
                    String imageUrl = downloadImageUrl(user.getImagePath());
                    user.setImageUrl(imageUrl);
                }
                user.setId(value.getId());

                userCallBack.onFetchUserComplete(user);
            }
        });
    }

    public void signOut(){
        this.auth.signOut();
    }

    public String downloadImageUrl(String imagePath){
        Task<Uri> downloadImageTask = this.storage.getReference().child(imagePath).getDownloadUrl();
        while (!downloadImageTask.isComplete() && !downloadImageTask.isCanceled());
        return downloadImageTask.getResult().toString();
    }

    public boolean uploadImage(Uri imageUri, String imagePath){
        try{
            UploadTask uploadTask = this.storage.getReference(imagePath).putFile(imageUri);
            while (!uploadTask.isComplete() && !uploadTask.isCanceled());
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage().toString());
            return false;
        }

    }

    public void fetchAllSellers(){
        this.mDatabase.collection(SELLERS_TABLE).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value == null) return;

                ArrayList<Seller> sellers = new ArrayList<>();
                for(DocumentSnapshot snapshot: value.getDocuments()){
                    Seller seller = snapshot.toObject(Seller.class);
                    seller.setId(seller.getId());
                    sellers.add(seller);
                }

                sellerCallBack.onFetchSellersComplete(sellers);
            }
        });
    }
}
