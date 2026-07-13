package com.example.diplomskiandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.models.WorkOrderPartItem;

import java.util.List;

public class WorkOrderPartAdapter extends RecyclerView.Adapter<WorkOrderPartAdapter.ViewHolder> {

    private final List<WorkOrderPartItem> parts;
    private final OnPartDeleteListener listener;
    private boolean completed = false;
    public WorkOrderPartAdapter(List<WorkOrderPartItem> parts,
                                OnPartDeleteListener listener) {
        this.parts = parts;
        this.listener = listener;
    }

    public interface OnPartDeleteListener {
        void onDelete(WorkOrderPartItem item);
    }

    @NonNull
    @Override
    public WorkOrderPartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work_order_part, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkOrderPartAdapter.ViewHolder holder, int position) {
        WorkOrderPartItem item = parts.get(position);

        holder.txtPartName.setText(item.getPart().getName());

        holder.txtQuantity.setText(
                item.getQuantity() + " kom × " + item.getUnitPrice() + " €"
        );

        holder.txtPartTotalPrice.setText(
                item.getTotalPrice() + " €"
        );

        holder.btnDeletePart.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(item);
            }
        });
        holder.btnDeletePart.setVisibility(
                completed ? View.GONE : View.VISIBLE
        );
    }

    @Override
    public int getItemCount() {
        return parts.size();
    }

    public void updateData(List<WorkOrderPartItem> newItems) {
        parts.clear();
        parts.addAll(newItems);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtPartName;
        TextView txtQuantity;
        TextView txtPartTotalPrice;
        ImageButton btnDeletePart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPartName = itemView.findViewById(R.id.txtPartName);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtPartTotalPrice = itemView.findViewById(R.id.txtPartTotalPrice);
            btnDeletePart = itemView.findViewById(R.id.btnDeletePart);
        }
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
        notifyDataSetChanged();
    }
}