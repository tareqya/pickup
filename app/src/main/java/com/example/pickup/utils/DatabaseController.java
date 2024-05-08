package com.example.pickup.utils;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pickup.callbacks.AuthCallBack;
import com.example.pickup.callbacks.ImageUrlDownloadListener;
import com.example.pickup.callbacks.ProductCallBack;
import com.example.pickup.callbacks.SellerCallBack;
import com.example.pickup.callbacks.UserCallBack;
import com.example.pickup.models.Product;
import com.example.pickup.models.Seller;
import com.example.pickup.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseController {
    public static String USERS_TABLE = "Users";
    public static String SELLERS_TABLE = "Sellers";
    public static String PRODUCTS_TABLE = "Products";

    private FirebaseAuth auth;
    private FirebaseFirestore mDatabase;
    private FirebaseStorage storage;

    private AuthCallBack authCallBack;
    private UserCallBack userCallBack;
    private SellerCallBack sellerCallBack;
    private ProductCallBack productCallBack;

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
    public void setProductCallBack(ProductCallBack productCallBack){
        this.productCallBack = productCallBack;
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
                user.setId(value.getId());
                if(user.getImagePath() != null){
                    downloadImageUrl(user.getImagePath(), new ImageUrlDownloadListener() {
                        @Override
                        public void onImageUrlDownloaded(String imageUrl) {
                            user.setImageUrl(imageUrl);
                            userCallBack.onFetchUserComplete(user);
                        }

                        @Override
                        public void onImageUrlDownloadFailed(String errorMessage) {

                        }
                    });
                }
            }
        });
    }

    public void signOut(){
        this.auth.signOut();
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
    public void downloadImageUrl(String imagePath, ImageUrlDownloadListener listener){
        this.storage.getReference().child(imagePath).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        listener.onImageUrlDownloaded(uri.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onImageUrlDownloadFailed(e.getMessage());
                    }
                });
    }

    public void fetchAllProducts(){
        this.mDatabase.collection(PRODUCTS_TABLE).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) return;

                AtomicInteger count = new AtomicInteger(value.size() - 1);
                ArrayList<Product> products = new ArrayList<>();
                for(DocumentSnapshot snapshot: value.getDocuments()){
                    Product product = snapshot.toObject(Product.class);
                    if(product.getImagePath() != null){
                        downloadImageUrl(product.getImagePath(), new ImageUrlDownloadListener() {
                            @Override
                            public void onImageUrlDownloaded(String imageUrl) {
                                product.setImageUrl(imageUrl);
                                products.add(product);

                                if(count.getAndDecrement() == 0){
                                    productCallBack.onFetchProductsComplete(products);
                                }
                            }

                            @Override
                            public void onImageUrlDownloadFailed(String errorMessage) {
                                // Handle download failure
                            }
                        });
                    } else {
                        products.add(product);
                        if (count.decrementAndGet() == 0) {
                            productCallBack.onFetchProductsComplete(products);
                        }
                    }
                }
            }
        });
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
