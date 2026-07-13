package com.example.diplomskiandroid.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.adapters.WorkOrderAdapter;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.WorkOrderApi;
import com.example.diplomskiandroid.models.WorkOrder;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkOrdersFragment extends Fragment {

    private RecyclerView rvWorkOrders;
    private TextView txtEmpty;

    private WorkOrderAdapter adapter;
    private final List<WorkOrder> workOrders = new ArrayList<>();
    private final List<WorkOrder> allWorkOrders = new ArrayList<>();

    private WorkOrderApi workOrderApi;
    Chip chipAll;
    Chip chipOpen;
    Chip chipCompleted;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_work_orders,
                container,
                false
        );

        rvWorkOrders = view.findViewById(R.id.rvWorkOrders);
        txtEmpty = view.findViewById(R.id.txtEmpty);

        chipAll = view.findViewById(R.id.chipAll);
        chipOpen = view.findViewById(R.id.chipOpen);
        chipCompleted = view.findViewById(R.id.chipCompleted);
        chipAll.setOnClickListener(v -> filterWorkOrders("ALL"));

        chipOpen.setOnClickListener(v -> filterWorkOrders("OPEN"));

        chipCompleted.setOnClickListener(v -> filterWorkOrders("COMPLETED"));


        rvWorkOrders.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        adapter = new WorkOrderAdapter(
                requireContext(),
                workOrders
        );

        rvWorkOrders.setAdapter(adapter);

        workOrderApi = ApiClient
                .getClient(requireContext())
                .create(WorkOrderApi.class);

        loadWorkOrders();

        return view;
    }
    private void filterWorkOrders(String filter) {

        workOrders.clear();

        switch (filter) {

            case "OPEN":

                for (WorkOrder workOrder : allWorkOrders) {

                    if (workOrder.getStatus().equals("OPEN")
                            || workOrder.getStatus().equals("IN_PROGRESS")) {

                        workOrders.add(workOrder);
                    }
                }

                break;

            case "COMPLETED":

                for (WorkOrder workOrder : allWorkOrders) {

                    if (workOrder.getStatus().equals("COMPLETED")) {
                        workOrders.add(workOrder);
                    }
                }

                break;

            default:

                workOrders.addAll(allWorkOrders);
                break;
        }

        adapter.notifyDataSetChanged();

        txtEmpty.setVisibility(
                workOrders.isEmpty()
                        ? View.VISIBLE
                        : View.GONE
        );
    }
    private void loadWorkOrders() {

        workOrderApi.getAllWorkOrders()
                .enqueue(new Callback<List<WorkOrder>>() {

                    @Override
                    public void onResponse(Call<List<WorkOrder>> call,
                                           Response<List<WorkOrder>> response) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            allWorkOrders.clear();
                            allWorkOrders.addAll(response.body());

                            workOrders.clear();
                            workOrders.addAll(allWorkOrders);
                            adapter.notifyDataSetChanged();

                            txtEmpty.setVisibility(
                                    workOrders.isEmpty()
                                            ? View.VISIBLE
                                            : View.GONE
                            );

                        } else {

                            Toast.makeText(
                                    requireContext(),
                                    "Greška kod dohvaćanja radnih naloga.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<WorkOrder>> call,
                                          Throwable t) {

                        Toast.makeText(
                                requireContext(),
                                t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorkOrders();
    }
}