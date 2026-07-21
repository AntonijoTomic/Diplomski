package com.example.diplomskiandroid.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.activities.AddServiceRequestActivity;
import com.example.diplomskiandroid.activities.AddVehicleActivity;
import com.example.diplomskiandroid.adapters.VehicleSummaryAdapter;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.UserApi;
import com.example.diplomskiandroid.models.MonthlyServiceCost;
import com.example.diplomskiandroid.models.UserDashboardResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView txtUserName, txtRole, txtViewAllVehicles;
    private TextView txtVehicleCount, txtActiveRequestCount, txtTotalServiceCost, txtVehiclePosition, txtPeriodTotalCost;
    private View cardAddVehicle,cardAddRequest;
    private ViewPager2 vehicleViewPager;
    private LineChart serviceCostsChart;
    private VehicleSummaryAdapter vehicleAdapter;
    private UserApi userApi;
    private ImageButton btnPreviousVehicle, btnNextVehicle;

    public HomeFragment() {
    }
    @Override
    public void onResume() {
        super.onResume();

        loadDashboard();
    }
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        loadUserData();
        loadDashboard();
    }

    private void initializeViews(View view) {
        txtUserName = view.findViewById(R.id.txtUserName);
        txtRole = view.findViewById(R.id.txtRole);

        txtVehicleCount = view.findViewById(R.id.txtVehicleCount);
        txtActiveRequestCount = view.findViewById(R.id.txtActiveRequestCount);
        txtTotalServiceCost = view.findViewById(R.id.txtTotalServiceCost);

        txtViewAllVehicles = view.findViewById(R.id.txtViewAllVehicles);

        vehicleViewPager = view.findViewById(R.id.vehicleViewPager);
        btnPreviousVehicle = view.findViewById(R.id.btnPreviousVehicle);
        btnNextVehicle = view.findViewById(R.id.btnNextVehicle);
        txtVehiclePosition = view.findViewById(R.id.txtVehiclePosition);

        serviceCostsChart = view.findViewById(R.id.serviceCostsChart);
        txtPeriodTotalCost = view.findViewById(R.id.txtPeriodTotalCost);

        cardAddVehicle = view.findViewById(R.id.cardAddVehicle);
        cardAddRequest = view.findViewById(R.id.cardAddRequest);

        cardAddVehicle.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddVehicleActivity.class);
            startActivity(intent);
        });

        cardAddRequest.setOnClickListener(v -> {
            Intent intent = new Intent(
                    requireContext(),
                    AddServiceRequestActivity.class
            );
            startActivity(intent);
        });

        userApi = ApiClient
                .getClient(requireContext())
                .create(UserApi.class);

        btnPreviousVehicle.setOnClickListener(v -> {
            int currentPosition = vehicleViewPager.getCurrentItem();

            if (currentPosition > 0) {
                vehicleViewPager.setCurrentItem(
                        currentPosition - 1,
                        true
                );
            }
        });

        btnNextVehicle.setOnClickListener(v -> {
            if (vehicleViewPager.getAdapter() == null) {
                return;
            }

            int currentPosition = vehicleViewPager.getCurrentItem();
            int lastPosition =
                    vehicleViewPager.getAdapter().getItemCount() - 1;

            if (currentPosition < lastPosition) {
                vehicleViewPager.setCurrentItem(
                        currentPosition + 1,
                        true
                );
            }
        });

        vehicleViewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        updateVehicleNavigation(position);
                    }
                }
        );
        ViewPager2 viewPager = requireActivity().findViewById(R.id.viewPager);
        txtViewAllVehicles.setOnClickListener(v ->
                viewPager.setCurrentItem(1, true));
        txtActiveRequestCount.setOnClickListener(v ->
                viewPager.setCurrentItem(2, true));
        txtVehicleCount.setOnClickListener(v ->
                viewPager.setCurrentItem(1, true));

    }

    private void updateVehicleNavigation(int position) {
        if (vehicleViewPager.getAdapter() == null) {
            return;
        }

        int vehicleCount = vehicleViewPager.getAdapter().getItemCount();

        if (vehicleCount == 0) {
            txtVehiclePosition.setText("0 / 0");

            btnPreviousVehicle.setEnabled(false);
            btnNextVehicle.setEnabled(false);

            btnPreviousVehicle.setAlpha(0.3f);
            btnNextVehicle.setAlpha(0.3f);

            return;
        }

        txtVehiclePosition.setText(
                String.format(
                        Locale.getDefault(),
                        "%d / %d",
                        position + 1,
                        vehicleCount
                )
        );

        boolean hasPrevious = position > 0;
        boolean hasNext = position < vehicleCount - 1;

        btnPreviousVehicle.setEnabled(hasPrevious);
        btnNextVehicle.setEnabled(hasNext);

        btnPreviousVehicle.setAlpha(hasPrevious ? 1f : 0.3f);
        btnNextVehicle.setAlpha(hasNext ? 1f : 0.3f);


    }

    private void loadUserData() {
        SharedPreferences preferences = requireActivity()
                .getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);

        String fullName = preferences.getString("fullName", "Korisnik");
        String role = preferences.getString("role", "USER");

        txtUserName.setText(fullName);

        if ("ADMIN".equalsIgnoreCase(role)) {
            txtRole.setText("Administrator");
        } else {
            txtRole.setText("Korisnik");
        }
    }

    private void loadDashboard() {
        userApi.getDashboard().enqueue(new Callback<UserDashboardResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<UserDashboardResponse> call,
                    @NonNull Response<UserDashboardResponse> response
            ) {
                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    displayDashboard(response.body());
                } else {
                    Toast.makeText(
                            requireContext(),
                            "Nije moguće učitati početnu stranicu.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<UserDashboardResponse> call,
                    @NonNull Throwable throwable
            ) {
                if (!isAdded()) {
                    return;
                }

                Toast.makeText(
                        requireContext(),
                        "Greška pri povezivanju sa serverom.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void displayDashboard(UserDashboardResponse dashboard) {


        txtVehicleCount.setText(
                String.valueOf(dashboard.getVehicleCount())
        );

        txtActiveRequestCount.setText(
                String.valueOf(dashboard.getActiveServiceRequestCount())
        );

        NumberFormat currencyFormat =
                NumberFormat.getCurrencyInstance(new Locale("hr", "HR"));

        txtTotalServiceCost.setText(
                currencyFormat.format(dashboard.getTotalServiceCost())
        );

        double periodTotal = 0;

        if (dashboard.getMonthlyServiceCosts() != null) {
            for (MonthlyServiceCost item : dashboard.getMonthlyServiceCosts()) {
                periodTotal += item.getTotalCost();
            }
        }

        NumberFormat currencyFormat2 =  NumberFormat.getCurrencyInstance(new Locale("hr", "HR"));

        txtPeriodTotalCost.setText(currencyFormat2.format(periodTotal));

        setupChart(dashboard.getMonthlyServiceCosts());
        if (dashboard.getVehicles() != null &&
                !dashboard.getVehicles().isEmpty()) {

            vehicleAdapter = new VehicleSummaryAdapter(
                    dashboard.getVehicles()
            );

            vehicleViewPager.setAdapter(vehicleAdapter);

            vehicleViewPager.setClipToPadding(false);
            vehicleViewPager.setClipChildren(false);
            vehicleViewPager.setOffscreenPageLimit(3);

            RecyclerView recyclerView =
                    (RecyclerView) vehicleViewPager.getChildAt(0);

            recyclerView.setClipToPadding(false);
            recyclerView.setClipChildren(false);
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

            CompositePageTransformer transformer =
                    new CompositePageTransformer();

            transformer.addTransformer(
                    new MarginPageTransformer(24)
            );

            transformer.addTransformer((page, position) -> {
                float absolutePosition = Math.abs(position);

                float scale =
                        0.88f + (1 - absolutePosition) * 0.12f;

                float alpha =
                        0.6f + (1 - absolutePosition) * 0.4f;

                page.setScaleY(scale);
                page.setAlpha(alpha);
            });

            vehicleViewPager.setPageTransformer(transformer);

            vehicleViewPager.setCurrentItem(0, false);
            updateVehicleNavigation(0);

        } else {
            txtVehiclePosition.setText("0 / 0");

            btnPreviousVehicle.setEnabled(false);
            btnNextVehicle.setEnabled(false);

            btnPreviousVehicle.setAlpha(0.3f);
            btnNextVehicle.setAlpha(0.3f);
        }
    }
    private void setupChart(List<MonthlyServiceCost> costs) {

        if (costs == null || costs.isEmpty()) {
            serviceCostsChart.clear();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < costs.size(); i++) {
            MonthlyServiceCost item = costs.get(i);

            entries.add(new Entry(i, (float) item.getTotalCost()));
            labels.add(item.getMonth());
        }

        int burgundy = ContextCompat.getColor(requireContext(), R.color.primary_burgundy);
        int white = ContextCompat.getColor(requireContext(), android.R.color.white);
        int gray = ContextCompat.getColor(requireContext(), R.color.text_gray);
        int gridColor = ContextCompat.getColor(requireContext(), R.color.background_light);

        LineDataSet dataSet = new LineDataSet(entries, "Troškovi");

        dataSet.setColor(burgundy);
        dataSet.setCircleColor(burgundy);
        dataSet.setCircleHoleColor(white);
        dataSet.setLineWidth(4f);
        dataSet.setCircleRadius(6f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(burgundy);
        dataSet.setFillAlpha(30);

        LineData data = new LineData(dataSet);

        serviceCostsChart.setData(data);
        serviceCostsChart.setDragEnabled(true);
        serviceCostsChart.setScaleEnabled(false);
        serviceCostsChart.setPinchZoom(false);
        serviceCostsChart.setDoubleTapToZoomEnabled(false);

        serviceCostsChart.setVisibleXRangeMaximum(6);
        serviceCostsChart.moveViewToX(entries.size() - 1);

        XAxis xAxis = serviceCostsChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(gray);
        xAxis.setTextSize(11f);
        xAxis.setAxisLineColor(gridColor);

        YAxis yAxisLeft = serviceCostsChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(true);
        yAxisLeft.setGridColor(gridColor);
        yAxisLeft.setTextColor(gray);
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setDrawAxisLine(false);

        serviceCostsChart.animateX(1500);
        serviceCostsChart.getAxisRight().setEnabled(false);
        serviceCostsChart.getDescription().setEnabled(false);
        serviceCostsChart.getLegend().setEnabled(false);

        serviceCostsChart.invalidate();
    }
}