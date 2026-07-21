package com.example.diplomskiandroid.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.adapters.AdminServiceRequestAdapter;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.ServiceRequestApi;
import com.example.diplomskiandroid.models.ServiceRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRequestsFragment extends Fragment {

    private RecyclerView rvServiceRequests;
    private ServiceRequestApi serviceRequestApi;

    public AdminRequestsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        rvServiceRequests = view.findViewById(R.id.rvServiceRequests);

        view.findViewById(R.id.btnAddServiceRequest).setVisibility(View.GONE);

        rvServiceRequests.setLayoutManager(new LinearLayoutManager(requireContext()));

        serviceRequestApi = ApiClient.getClient(requireContext())
                .create(ServiceRequestApi.class);

        loadRequests();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRequests();
    }

    private void loadRequests() {

        serviceRequestApi.getAllRequests().enqueue(new Callback<List<ServiceRequest>>() {

            @Override
            public void onResponse(Call<List<ServiceRequest>> call,
                                   Response<List<ServiceRequest>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    AdminServiceRequestAdapter adapter =
                            new AdminServiceRequestAdapter(response.body(), requireContext());

                    rvServiceRequests.setAdapter(adapter);

                } else {

                    Toast.makeText(requireContext(),
                            "Greška: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ServiceRequest>> call,
                                  Throwable t) {

                Toast.makeText(requireContext(),
                        t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}