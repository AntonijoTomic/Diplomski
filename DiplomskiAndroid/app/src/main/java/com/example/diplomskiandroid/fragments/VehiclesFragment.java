package com.example.diplomskiandroid.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.activities.AddVehicleActivity;
import com.example.diplomskiandroid.adapters.VehicleAdapter;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.VehicleApi;
import com.example.diplomskiandroid.models.Vehicle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehiclesFragment extends Fragment {

    private TextView txtVehicleCount;
    private RecyclerView rvVehicles;
    private FloatingActionButton btnAddVehicle;
    private VehicleApi vehicleApi;

    public VehiclesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_vehicles, container, false);

        txtVehicleCount = view.findViewById(R.id.txtVehicleCount);
        rvVehicles = view.findViewById(R.id.rvVehicles);
        btnAddVehicle = view.findViewById(R.id.btnAddVehicle);

        rvVehicles.setLayoutManager(new LinearLayoutManager(requireContext()));

        vehicleApi = ApiClient.getClient().create(VehicleApi.class);

        btnAddVehicle.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddVehicleActivity.class);
            startActivity(intent);
        });

        loadVehicles();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadVehicles();
    }

    private void loadVehicles() {
        SharedPreferences preferences = requireActivity()
                .getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);

        int userId = preferences.getInt("userId", 0);

        vehicleApi.getVehiclesByUser(userId).enqueue(new Callback<List<Vehicle>>() {
            @Override
            public void onResponse(Call<List<Vehicle>> call, Response<List<Vehicle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Vehicle> vehicles = response.body();

                    txtVehicleCount.setText(String.valueOf(vehicles.size()));

                    VehicleAdapter adapter = new VehicleAdapter(vehicles);
                    rvVehicles.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(),
                            "Greška kod dohvaćanja vozila.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Vehicle>> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Greška povezivanja: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}