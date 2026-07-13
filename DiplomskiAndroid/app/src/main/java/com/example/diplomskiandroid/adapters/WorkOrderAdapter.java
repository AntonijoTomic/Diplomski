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
import com.example.diplomskiandroid.StatusHelper;
import com.example.diplomskiandroid.activities.WorkOrderDetailsActivity;
import com.example.diplomskiandroid.models.WorkOrder;

import java.util.List;

public class WorkOrderAdapter extends RecyclerView.Adapter<WorkOrderAdapter.ViewHolder> {

    private final Context context;
    private final List<WorkOrder> workOrders;

    public WorkOrderAdapter(Context context, List<WorkOrder> workOrders) {
        this.context = context;
        this.workOrders = workOrders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work_order, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WorkOrder workOrder = workOrders.get(position);

        holder.txtOrderNumber.setText(workOrder.getOrderNumber());

        holder.txtVehicle.setText(
                workOrder.getServiceRequest().getVehicle().getBrand()
                        + " "
                        + workOrder.getServiceRequest().getVehicle().getModel()
        );

        holder.txtOwner.setText(
                workOrder.getServiceRequest().getUser().getFirstName()
                        + " "
                        + workOrder.getServiceRequest().getUser().getLastName()
        );

        StatusHelper.applyStatus(
                holder.txtStatus,
                workOrder.getStatus()
        );

        holder.txtEstimatedCost.setText(
                workOrder.getEstimatedCost() + " €"
        );

        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(context, WorkOrderDetailsActivity.class);

            intent.putExtra("workOrderId", workOrder.getId());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return workOrders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtOrderNumber;
        TextView txtVehicle;
        TextView txtOwner;
        TextView txtStatus;
        TextView txtEstimatedCost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtOrderNumber = itemView.findViewById(R.id.txtOrderNumber);
            txtVehicle = itemView.findViewById(R.id.txtVehicle);
            txtOwner = itemView.findViewById(R.id.txtOwner);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtEstimatedCost = itemView.findViewById(R.id.txtEstimatedCost);
        }
    }
}