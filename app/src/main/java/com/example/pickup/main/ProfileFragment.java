package com.example.pickup.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pickup.R;
import com.example.pickup.auth.LoginActivity;
import com.example.pickup.models.User;
import com.example.pickup.utils.DatabaseController;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private Context context;
    private User currentUser;
    private CircleImageView profile_CIV_image;
    private TextView profile_TV_name;
    private TextView profile_TV_score;
    private CardView profile_CV_editDetails;
    private CardView profile_CV_logout;
    private DatabaseController databaseController;

    public ProfileFragment(Context context) {
        this.context = context;
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
        displayUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        findViews(view);
        initVars();
        return view;
    }

    private void findViews(View view) {
        profile_CV_editDetails = view.findViewById(R.id.profile_CV_editDetails);
        profile_TV_score = view.findViewById(R.id.profile_TV_score);
        profile_TV_name = view.findViewById(R.id.profile_TV_name);
        profile_CIV_image = view.findViewById(R.id.profile_CIV_image);
        profile_CV_logout = view.findViewById(R.id.profile_CV_logout);
    }

    private void initVars() {
        databaseController = new DatabaseController();

        profile_CV_editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateProfileActivity.class);
                intent.putExtra("USER", currentUser);
                startActivity(intent);
            }
        });

        profile_CV_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseController.signOut();
                Intent intent = new Intent((Activity)context, LoginActivity.class);
                startActivity(intent);
                ((Activity) context).finish();
            }
        });

    }

    private void displayUI() {
        if(this.currentUser.getImageUrl() != null){
            Glide.with(context).load(this.currentUser.getImageUrl()).into(profile_CIV_image);
        }
        // Create a DecimalFormat object with the desired pattern
        DecimalFormat df = new DecimalFormat("#.##"); // Two digits after the decimal point
        // Format the number using DecimalFormat
        String formattedNumber = df.format(this.currentUser.getScore());
        profile_TV_score.setText(formattedNumber);
        String fullName = this.currentUser.getFirstName() + " " +this.currentUser.getLastName();
        profile_TV_name.setText(fullName);

    }
}