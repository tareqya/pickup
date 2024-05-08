package com.example.pickup.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pickup.R;
import com.example.pickup.callbacks.UserCallBack;
import com.example.pickup.models.Product;
import com.example.pickup.models.User;
import com.example.pickup.utils.DatabaseController;
import com.google.android.gms.tasks.Task;

public class ProductActivity extends AppCompatActivity {
    private TextView product_TV_name;
    private TextView product_TV_price;
    private TextView product_TV_description;
    private ImageView product_IV_image;
    private Button product_BTN_order;
    private DatabaseController databaseController;
    private User currentUser;
    private Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("USER");
        product = (Product) intent.getSerializableExtra("PRODUCT");
        findViews();
        intiVars();
    }

    private void findViews() {
        product_IV_image = findViewById(R.id.product_IV_image);
        product_TV_description = findViewById(R.id.product_TV_description);
        product_TV_price = findViewById(R.id.product_TV_price);
        product_TV_name = findViewById(R.id.product_TV_name);
        product_BTN_order = findViewById(R.id.product_BTN_order);
    }

    private void intiVars() {
        databaseController = new DatabaseController();
        databaseController.setUserCallBack(new UserCallBack() {
            @Override
            public void onSaveUserComplete(Task<Void> task) {
                Toast.makeText(ProductActivity.this, "Order added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFetchUserComplete(User user) {

            }
        });

        displayUI();
        product_BTN_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUser.getScore() >= product.getPrice()){
                    currentUser.setScore(currentUser.getScore() - product.getPrice());
                    databaseController.saveUser(currentUser);
                }else{
                    Toast.makeText(ProductActivity.this, "You don't have enough point to order this product!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayUI() {
        product_TV_name.setText(product.getName());
        product_TV_price.setText(product.getPrice() + "");
        product_TV_description.setText(product.getDescription());
        if(product.getImageUrl() != null){
            Glide.with(ProductActivity.this).load(product.getImageUrl()).into(product_IV_image);
        }
    }


}