package com.example.diplomskiandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.models.WorkOrderServiceItem;

import java.util.List;

public class WorkOrderServiceAdapter extends RecyclerView.Adapter<WorkOrderServiceAdapter.ViewHolder> {

    private final List<WorkOrderServiceItem> services;
    private final OnServiceDeleteListener listener;
    private boolean completed = false;
     public WorkOrderServiceAdapter(List<WorkOrderServiceItem> services,
                                   OnServiceDeleteListener listener) {
        this.services = services;
        this.listener = listener;
    }
    public interface OnServiceDeleteListener {
        void onDelete(WorkOrderServiceItem item);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work_order_service, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WorkOrderServiceItem item = services.get(position);

        holder.txtServiceName.setText(
                item.getService().getName()
        );

        holder.txtHours.setText(
                item.getHours() + " h × " +
                        item.getHourlyRate() + " €"
        );

        holder.txtTotalPrice.setText(
                item.getTotalPrice() + " €"
        );

        holder.btnDeleteService.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(item);
            }
        });
        holder.btnDeleteService.setVisibility(
                completed ? View.GONE : View.VISIBLE
        );
    }

    @Override
    public int getItemCount() {
        return services.size();
    }
    public void updateData(List<WorkOrderServiceItem> newItems) {
        services.clear();
        services.addAll(newItems);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtServiceName;
        TextView txtHours;
        TextView txtTotalPrice;
        ImageButton btnDeleteService;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtServiceName = itemView.findViewById(R.id.txtServiceName);
            txtHours = itemView.findViewById(R.id.txtHours);
            txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
            btnDeleteService = itemView.findViewById(R.id.btnDeleteService);

        }
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
        notifyDataSetChanged();
    }
}