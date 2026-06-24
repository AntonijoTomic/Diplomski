package com.example.diplomskiandroid.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.diplomskiandroid.R;

public class HomeFragment extends Fragment {

    private TextView txtUserName;
    private TextView txtRole;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        txtUserName = view.findViewById(R.id.txtUserName);
        txtRole = view.findViewById(R.id.txtRole);

        SharedPreferences preferences = requireActivity()
                .getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);

        String fullName = preferences.getString("fullName", "Korisnik");
        String role = preferences.getString("role", "USER");

        txtUserName.setText(fullName);

        if (role.equals("ADMIN")) {
            txtRole.setText("Administrator");
        } else {
            txtRole.setText("Korisnik");
        }

        return view;
    }
}