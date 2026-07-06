package com.example.diplomskiandroid.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.activities.AddServiceRequestActivity;
import com.example.diplomskiandroid.adapters.ServiceRequestAdapter;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.ServiceRequestApi;
import com.example.diplomskiandroid.models.ServiceRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestsFragment extends Fragment {

    private RecyclerView rvServiceRequests;
    private FloatingActionButton btnAddServiceRequest;
    private ServiceRequestApi serviceRequestApi;

    public RequestsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        rvServiceRequests = view.findViewById(R.id.rvServiceRequests);
        btnAddServiceRequest = view.findViewById(R.id.btnAddServiceRequest);

        rvServiceRequests.setLayoutManager(new LinearLayoutManager(requireContext()));

        serviceRequestApi = ApiClient.getClient(requireContext()).create(ServiceRequestApi.class);

        btnAddServiceRequest.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddServiceRequestActivity.class);
            startActivity(intent);
        });

        loadServiceRequests();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadServiceRequests();
    }

    private void loadServiceRequests() {
        SharedPreferences preferences = requireActivity()
                .getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);

        int userId = preferences.getInt("userId", 0);

        serviceRequestApi.getRequestsByUser(userId).enqueue(new Callback<List<ServiceRequest>>() {
            @Override
            public void onResponse(Call<List<ServiceRequest>> call, Response<List<ServiceRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ServiceRequest> requests = response.body();

                    ServiceRequestAdapter adapter = new ServiceRequestAdapter(requests, requireContext());
                    rvServiceRequests.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(),
                            "Greška kod dohvaćanja servisnih zahtjeva: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ServiceRequest>> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Greška povezivanja: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}