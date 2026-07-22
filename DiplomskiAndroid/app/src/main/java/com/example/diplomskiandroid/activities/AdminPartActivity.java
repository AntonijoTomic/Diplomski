package com.example.diplomskiandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.adapters.PartAdapter;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.PartApi;
import com.example.diplomskiandroid.models.Part;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPartActivity extends AppCompatActivity {

    private RecyclerView rvParts;
    private TextInputEditText edtSearch;
    private MaterialButton btnShowAll;
    private MaterialButton btnLowStock;
    private MaterialButton btnAddPart;
    private TextView txtPartCount;
    private TextView btnBack;

    private PartAdapter adapter;
    private PartApi partApi;

    private final List<Part> allParts = new ArrayList<>();
    private final List<Part> filteredParts = new ArrayList<>();

    private boolean lowStockOnly = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_parts);

        rvParts = findViewById(R.id.rvParts);
        edtSearch = findViewById(R.id.edtSearch);
        btnShowAll = findViewById(R.id.btnShowAll);
        btnLowStock = findViewById(R.id.btnLowStock);
        btnAddPart = findViewById(R.id.btnAddPart);
        txtPartCount = findViewById(R.id.txtPartCount);
        btnBack = findViewById(R.id.btnBack);

        rvParts.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PartAdapter(filteredParts);
        rvParts.setAdapter(adapter);

        partApi = ApiClient
                .getClient(this)
                .create(PartApi.class);

        btnBack.setOnClickListener(v -> finish());

        btnShowAll.setOnClickListener(v -> {
            lowStockOnly = false;

            btnShowAll.setSelected(true);
            btnLowStock.setSelected(false);

            applyFilters();
        });

        btnLowStock.setOnClickListener(v -> {
            lowStockOnly = true;

            btnLowStock.setSelected(true);
            btnShowAll.setSelected(false);

            applyFilters();
        });
        boolean showLowStock =
                getIntent().getBooleanExtra("showLowStock", false);

        if (showLowStock) {
            lowStockOnly = true;

            btnLowStock.setSelected(true);
            btnShowAll.setSelected(false);
        } else {
            lowStockOnly = false;

            btnShowAll.setSelected(true);
            btnLowStock.setSelected(false);
        }
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnAddPart.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPartActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadParts();
    }

    private void loadParts() {

        partApi.getAllParts().enqueue(new Callback<List<Part>>() {

            @Override
            public void onResponse(Call<List<Part>> call,
                                   Response<List<Part>> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(
                            AdminPartActivity.this,
                            "Greška pri dohvaćanju dijelova.",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                allParts.clear();
                allParts.addAll(response.body());

                applyFilters();
            }

            @Override
            public void onFailure(Call<List<Part>> call, Throwable t) {

                Toast.makeText(
                        AdminPartActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void applyFilters() {

        filteredParts.clear();

        String search = "";

        if (edtSearch.getText() != null) {
            search = edtSearch.getText()
                    .toString()
                    .trim()
                    .toLowerCase();
        }

        for (Part part : allParts) {

            if (lowStockOnly &&
                    part.getStockQuantity() > part.getMinimumStock()) {
                continue;
            }

            boolean matchesSearch =
                    part.getName().toLowerCase().contains(search)
                            || (part.getManufacturer() != null &&
                            part.getManufacturer()
                                    .toLowerCase()
                                    .contains(search));

            if (matchesSearch) {
                filteredParts.add(part);
            }
        }

        txtPartCount.setText(filteredParts.size() + " dijelova");

        adapter.notifyDataSetChanged();
    }
}