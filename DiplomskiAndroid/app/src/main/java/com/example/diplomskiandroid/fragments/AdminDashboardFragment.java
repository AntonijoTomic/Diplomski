package com.example.diplomskiandroid.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.adapters.LowStockPartAdapter;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.DashboardApi;
import com.example.diplomskiandroid.models.DashboardSummary;
import com.example.diplomskiandroid.models.MonthlyServiceCost;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class AdminDashboardFragment extends Fragment {

    private TextView txtUserCount;
    private TextView txtVehicleCount;
    private TextView txtServiceRequestCount;
    private TextView txtWorkOrderCount;
    private TextView txtPendingRequestCount;
    private TextView txtApprovedRequestCount;
    private TextView txtCurrentMonthRevenue;
    private TextView txtLowStockSummaryCount;
    private TextView txtRevenueTotal;
    private TextView txtLowStockCount;
    private DashboardApi dashboardApi;
    private BarChart barChartRevenue;
    private RecyclerView rvLowStockParts;

    public AdminDashboardFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_admin_dashboard,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        txtUserCount = view.findViewById(R.id.txtUserCount);
        txtVehicleCount = view.findViewById(R.id.txtVehicleCount);
        txtServiceRequestCount =
                view.findViewById(R.id.txtServiceRequestCount);
        txtWorkOrderCount =
                view.findViewById(R.id.txtWorkOrderCount);

        txtPendingRequestCount =
                view.findViewById(R.id.txtPendingRequestCount);

        txtApprovedRequestCount =
                view.findViewById(R.id.txtApprovedRequestCount);

        txtCurrentMonthRevenue =
                view.findViewById(R.id.txtCurrentMonthRevenue);

        txtLowStockSummaryCount =
                view.findViewById(R.id.txtLowStockSummaryCount);
        txtRevenueTotal =
                view.findViewById(R.id.txtRevenueTotal);
        txtLowStockCount =
                view.findViewById(R.id.txtLowStockCount);

        dashboardApi = ApiClient
                .getClient(requireContext())
                .create(DashboardApi.class);
        barChartRevenue =
                view.findViewById(R.id.chartRevenue);
        rvLowStockParts = view.findViewById(R.id.rvLowStockParts);
        rvLowStockParts.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboard();
    }

    private void loadDashboard() {
        dashboardApi.getSummary().enqueue(
                new Callback<DashboardSummary>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<DashboardSummary> call,
                            @NonNull Response<DashboardSummary> response
                    ) {
                        if (!isAdded()) {
                            return;
                        }

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            Toast.makeText(
                                    requireContext(),
                                    "Nije moguće učitati dashboard.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        DashboardSummary summary = response.body();
                        double lastSixMonthsRevenue = 0;

                        for (MonthlyServiceCost item : summary.getMonthlyRevenue()) {
                            lastSixMonthsRevenue += item.getTotalCost();
                        }

                        txtUserCount.setText(
                                String.valueOf(summary.getUserCount())
                        );

                        txtVehicleCount.setText(
                                String.valueOf(summary.getVehicleCount())
                        );

                        txtServiceRequestCount.setText(
                                String.valueOf(
                                        summary.getServiceRequestCount()
                                )
                        );

                        txtWorkOrderCount.setText(
                                String.valueOf(
                                        summary.getWorkOrderCount()
                                )
                        );

                        txtPendingRequestCount.setText(
                                String.valueOf(
                                        summary.getPendingServiceRequestCount()
                                )
                        );

                        txtApprovedRequestCount.setText(
                                String.valueOf(
                                        summary.getApprovedServiceRequestCount()
                                )
                        );

                        txtCurrentMonthRevenue.setText(
                                String.format(
                                        Locale.getDefault(),
                                        "%.2f €",
                                        summary.getCurrentMonthRevenue()
                                )
                        );

                        txtLowStockSummaryCount.setText(
                                String.valueOf(
                                        summary.getLowStockPartCount()
                                )
                        );
                        txtRevenueTotal.setText(
                                String.format(
                                        Locale.getDefault(),
                                        "%.2f €",
                                        lastSixMonthsRevenue
                                )
                        );
                        setupRevenueChart(summary.getMonthlyRevenue());
                        txtLowStockCount.setText(
                                String.valueOf(
                                        summary.getLowStockPartCount() + " dijelova na niskoj zalihi"
                                )
                        );
                        rvLowStockParts.setAdapter(
                                new LowStockPartAdapter(summary.getLowStockParts())
                        );
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<DashboardSummary> call,
                            @NonNull Throwable throwable
                    ) {
                        if (!isAdded()) {
                            return;
                        }

                        Toast.makeText(
                                requireContext(),
                                "Greška pri povezivanju s poslužiteljem.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

    private void setupRevenueChart(
            List<MonthlyServiceCost> monthlyRevenue
    ) {
        if (monthlyRevenue == null || monthlyRevenue.isEmpty()) {
            barChartRevenue.clear();
            barChartRevenue.setNoDataText(
                    "Nema podataka o prihodima."
            );
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> monthLabels = new ArrayList<>();

        for (int i = 0; i < monthlyRevenue.size(); i++) {
            MonthlyServiceCost item = monthlyRevenue.get(i);

            entries.add(
                    new BarEntry(
                            i,
                            (float) item.getTotalCost()
                    )
            );

            monthLabels.add(item.getMonth());
        }

        BarDataSet dataSet = new BarDataSet(
                entries,
                "Prihod"
        );

        dataSet.setColor(
                ContextCompat.getColor(requireContext(), R.color.primary_burgundy)
        );

        dataSet.setValueTextColor(
                ContextCompat.getColor(requireContext(), R.color.text_dark)
        );

        dataSet.setValueTextSize(11f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        XAxis xAxis = barChartRevenue.getXAxis();

        xAxis.setValueFormatter(
                new IndexAxisValueFormatter(monthLabels)
        );

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.text_gray)
        );

        barChartRevenue.getAxisLeft().setTextColor(
                ContextCompat.getColor(requireContext(), R.color.text_gray)
        );

        barChartRevenue.getAxisLeft().setGridColor(
                ContextCompat.getColor(requireContext(), R.color.border_light)
        );
        barChartRevenue.getAxisRight().setEnabled(false);

        barChartRevenue.getAxisLeft().setAxisMinimum(0f);

        barChartRevenue.getDescription().setEnabled(false);
        barChartRevenue.getLegend().setEnabled(false);

        barChartRevenue.setScaleEnabled(false);
        barChartRevenue.setDoubleTapToZoomEnabled(false);

        barChartRevenue.setData(barData);
        barChartRevenue.invalidate();
    }
}