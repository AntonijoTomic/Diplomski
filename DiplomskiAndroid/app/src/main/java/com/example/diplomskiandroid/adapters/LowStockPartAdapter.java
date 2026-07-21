package com.example.diplomskiandroid.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.models.Part;

import java.util.List;
public class LowStockPartAdapter extends RecyclerView.Adapter<LowStockPartAdapter.ViewHolder> {

    private final List<Part> parts;

    public LowStockPartAdapter(List<Part> parts) {
        this.parts = parts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_low_stock_part, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Part part = parts.get(position);

        holder.txtName.setText(part.getName());
        holder.txtStock.setText(
                part.getStockQuantity() + " / " + part.getMinimumStock()
        );
    }

    @Override
    public int getItemCount() {
        return parts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        TextView txtStock;

        ViewHolder(View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtStock = itemView.findViewById(R.id.txtStock);
        }
    }
}
