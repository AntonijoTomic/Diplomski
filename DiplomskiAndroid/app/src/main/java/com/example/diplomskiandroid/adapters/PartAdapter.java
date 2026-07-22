package com.example.diplomskiandroid.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.activities.AddPartActivity;
import com.example.diplomskiandroid.models.Part;

import java.util.List;
import java.util.Locale;

public class PartAdapter extends RecyclerView.Adapter<PartAdapter.ViewHolder> {

    private final List<Part> parts;

    public PartAdapter(List<Part> parts) {
        this.parts = parts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_part, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Part part = parts.get(position);

        holder.txtPartName.setText(part.getName());

        holder.txtManufacturer.setText(
                part.getManufacturer()
        );

        holder.txtPartPrice.setText(
                String.format(
                        Locale.getDefault(),
                        "%.2f €",
                        part.getPrice()
                )
        );

        holder.txtStockQuantity.setText(
                String.valueOf(
                        part.getStockQuantity()
                )
        );

        holder.txtMinimumStock.setText(
                "Minimum: " + part.getMinimumStock()
        );

        if (part.getStockQuantity() <= part.getMinimumStock()) {

            holder.txtLowStockWarning.setVisibility(View.VISIBLE);

            holder.txtStockQuantity.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.warning_orange
                    )
            );

        } else {

            holder.txtLowStockWarning.setVisibility(View.GONE);

            holder.txtStockQuantity.setTextColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.text_dark
                    )
            );
        }
        holder.itemView.setOnLongClickListener(v -> {

            Intent intent = new Intent(
                    holder.itemView.getContext(),
                    AddPartActivity.class
            );

            intent.putExtra("partId", part.getId());
            intent.putExtra("name", part.getName());
            intent.putExtra("manufacturer", part.getManufacturer());
            intent.putExtra("price", part.getPrice());
            intent.putExtra("stockQuantity", part.getStockQuantity());
            intent.putExtra("minimumStock", part.getMinimumStock());

            holder.itemView.getContext().startActivity(intent);

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return parts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtPartName;
        TextView txtManufacturer;
        TextView txtPartPrice;
        TextView txtStockQuantity;
        TextView txtMinimumStock;
        TextView txtLowStockWarning;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPartName = itemView.findViewById(R.id.txtPartName);
            txtManufacturer = itemView.findViewById(R.id.txtManufacturer);
            txtPartPrice = itemView.findViewById(R.id.txtPartPrice);
            txtStockQuantity = itemView.findViewById(R.id.txtStockQuantity);
            txtMinimumStock = itemView.findViewById(R.id.txtMinimumStock);
            txtLowStockWarning = itemView.findViewById(R.id.txtLowStockWarning);
        }
    }
}