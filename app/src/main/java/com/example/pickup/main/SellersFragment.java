package com.example.pickup.main;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pickup.R;

public class SellersFragment extends Fragment {

    private Context context;

    public SellersFragment(Context context) {
        this.context = context;
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
    }

    private void initVars() {
    }
}