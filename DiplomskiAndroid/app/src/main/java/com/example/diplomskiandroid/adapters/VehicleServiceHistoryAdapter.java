package com.example.diplomskiandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.activities.WorkOrderDetailsActivity;
import com.example.diplomskiandroid.models.WorkOrder;

import java.util.List;

public class VehicleServiceHistoryAdapter
        extends RecyclerView.Adapter<VehicleServiceHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<WorkOrder> workOrders;

    public VehicleServiceHistoryAdapter(Context context,
                                        List<WorkOrder> workOrders) {
        this.context = context;
        this.workOrders = workOrders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_vehicle_service_history,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        WorkOrder workOrder = workOrders.get(position);

        holder.txtHistoryOrderNumber.setText(
                workOrder.getOrderNumber()
        );

        holder.txtHistoryDate.setText(
                formatDate(workOrder.getClosedAt())
        );

        String report = workOrder.getFinalReport();

        if (report == null || report.trim().isEmpty()) {
            holder.txtHistoryReport.setText(
                    "Završni izvještaj nije unesen."
            );
        } else {
            holder.txtHistoryReport.setText(report);
        }

        holder.txtHistoryCost.setText(
                workOrder.getFinalCost() + " €"
        );

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    WorkOrderDetailsActivity.class
            );

            intent.putExtra(
                    "workOrderId",
                    workOrder.getId()
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return workOrders.size();
    }

    public void updateData(List<WorkOrder> newItems) {
        workOrders.clear();
        workOrders.addAll(newItems);
        notifyDataSetChanged();
    }

    private String formatDate(String date) {

        if (date == null || date.length() < 10) {
            return "-";
        }

        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);

        return day + "." + month + "." + year + ".";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtHistoryOrderNumber;
        TextView txtHistoryDate;
        TextView txtHistoryReport;
        TextView txtHistoryCost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtHistoryOrderNumber =
                    itemView.findViewById(
                            R.id.txtHistoryOrderNumber
                    );

            txtHistoryDate =
                    itemView.findViewById(
                            R.id.txtHistoryDate
                    );

            txtHistoryReport =
                    itemView.findViewById(
                            R.id.txtHistoryReport
                    );

            txtHistoryCost =
                    itemView.findViewById(
                            R.id.txtHistoryCost
                    );
        }
    }
}