package com.example.pickup.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.pickup.R;
import com.example.pickup.callbacks.UserCallBack;
import com.example.pickup.models.User;
import com.example.pickup.utils.DatabaseController;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private StoreFragment storeFragment;
    private ProfileFragment profileFragment;
    private SellersFragment sellersFragment;
    private FrameLayout main_frame_store, main_frame_profile, main_frame_sellers;
    private BottomNavigationView home_BN;
    private DatabaseController databaseController;
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initVars();
    }

    private void findViews() {
        main_frame_store = findViewById(R.id.main_frame_store);
        main_frame_profile = findViewById(R.id.main_frame_profile);
        main_frame_sellers = findViewById(R.id.main_frame_sellers);
        home_BN = findViewById(R.id.home_BN);
    }

    private void initVars() {
        databaseController = new DatabaseController();

        databaseController.setUserCallBack(new UserCallBack() {
            @Override
            public void onSaveUserComplete(Task<Void> task) {

            }

            @Override
            public void onFetchUserComplete(User user) {
                currentUser = user;
                profileFragment.setCurrentUser(user);
                sellersFragment.setCurrentUser(user);
            }
        });

        home_BN.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.profile){
                    main_frame_profile.setVisibility(View.VISIBLE);
                    main_frame_sellers.setVisibility(View.INVISIBLE);
                    main_frame_store.setVisibility(View.INVISIBLE);
                } else if(item.getItemId() == R.id.sellers){
                    main_frame_profile.setVisibility(View.INVISIBLE);
                    main_frame_sellers.setVisibility(View.VISIBLE);
                    main_frame_store.setVisibility(View.INVISIBLE);
                } else if(item.getItemId() == R.id.store){
                    main_frame_profile.setVisibility(View.INVISIBLE);
                    main_frame_sellers.setVisibility(View.INVISIBLE);
                    main_frame_store.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        String uid = databaseController.currentUser().getUid();
        databaseController.fetchUser(uid);
        connectFragments();
    }

    private void connectFragments() {

        storeFragment = new StoreFragment(this);
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame_store, storeFragment).commit();

        sellersFragment = new SellersFragment(this);
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame_sellers, sellersFragment).commit();

        profileFragment = new ProfileFragment(this);
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame_profile, profileFragment).commit();

        main_frame_profile.setVisibility(View.INVISIBLE);
        main_frame_sellers.setVisibility(View.INVISIBLE);


    }


}