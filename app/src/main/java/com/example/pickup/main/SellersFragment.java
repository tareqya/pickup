package com.example.pickup.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pickup.R;
import com.example.pickup.adapters.SellerAdapter;
import com.example.pickup.callbacks.SellerCallBack;
import com.example.pickup.callbacks.UserCallBack;
import com.example.pickup.models.Seller;
import com.example.pickup.models.User;
import com.example.pickup.utils.DatabaseController;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SellersFragment extends Fragment {
    public static double SCORE = 100;
    private Context context;
    private RecyclerView fSellers_RV_sellers;
    private FloatingActionButton fSellers_FAB_AddCode;
    private DatabaseController databaseController;
    private User currentUser;


    public SellersFragment(Context context) {
        this.context = context;
    }

    public void setCurrentUser(User currentUser){
        this.currentUser = currentUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sellers, container, false);
        findViews(view);
        initVars();
        return view;
    }

    private void findViews(View view) {
        fSellers_RV_sellers = view.findViewById(R.id.fSellers_RV_sellers);
        fSellers_FAB_AddCode = view.findViewById(R.id.fSellers_FAB_AddCode);
    }

    private void initVars() {
        fSellers_FAB_AddCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogWithTextBox(context);
            }
        });


        databaseController = new DatabaseController();

        databaseController.setUserCallBack(new UserCallBack() {
            @Override
            public void onSaveUserComplete(Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Score updated successfully", Toast.LENGTH_SHORT).show();
                }else{
                    String err = task.getException().getMessage().toString();
                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFetchUserComplete(User user) {

            }
        });
        databaseController.setSellersCallBack(new SellerCallBack() {
            @Override
            public void onFetchSellersComplete(ArrayList<Seller> sellers) {
                SellerAdapter sellersAdapter = new SellerAdapter(context, sellers);
                fSellers_RV_sellers.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                fSellers_RV_sellers.setHasFixedSize(true);
                fSellers_RV_sellers.setItemAnimator(new DefaultItemAnimator());
                fSellers_RV_sellers.setAdapter(sellersAdapter);
            }
        });

        databaseController.fetchAllSellers();
    }

    private void showDialogWithTextBox(Context context) {
        // Create an alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Enter Seller Code");

        final EditText input = new EditText(context);
        alertDialogBuilder.setView(input);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String code = input.getText().toString();

                // update score
                currentUser.setScore(currentUser.getScore() + SCORE);
                databaseController.saveUser(currentUser);
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing if canceled
                dialog.cancel();
            }
        });

        // Create and show the dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}