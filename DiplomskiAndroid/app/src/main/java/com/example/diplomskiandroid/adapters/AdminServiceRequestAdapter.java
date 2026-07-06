package com.example.diplomskiandroid.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.activities.AdminServiceRequestDetailsActivity;
import com.example.diplomskiandroid.activities.ServiceRequestDetailsActivity;
import com.example.diplomskiandroid.models.ServiceRequest;

import java.util.List;

public class AdminServiceRequestAdapter extends RecyclerView.Adapter<AdminServiceRequestAdapter.ViewHolder> {

    private final List<ServiceRequest> requests;
    private final Context context;

    public AdminServiceRequestAdapter(List<ServiceRequest> requests, Context context) {
        this.requests = requests;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_request, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceRequest request = requests.get(position);

        holder.txtRequestTitle.setText(
                request.getVehicle().getBrand() + " " +
                        request.getVehicle().getModel() + " • " +
                        request.getVehicle().getLicensePlate()
        );
        holder.txtVehicle.setText(
                request.getServiceType() != null && !request.getServiceType().isEmpty()
                        ? request.getServiceType()
                        : "Servisni zahtjev"
        );

        holder.txtDescription.setText(request.getProblemDescription());
        holder.txtCreatedAt.setText(formatDate(request.getCreatedAt()));
        holder.txtPriority.setText(request.getUrgency() != null ? request.getUrgency() : "-");
        holder.txtRequestStatus.setText(formatStatus(request.getStatus()));
        setStatusBackground(holder, request.getStatus());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminServiceRequestDetailsActivity.class);
            intent.putExtra("requestId", request.getId());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            Toast.makeText(context,
                    "Obrada zahtjeva dolazi u sljedećem koraku.",
                    Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    private String formatStatus(String status) {
        if (status == null) return "-";

        switch (status) {
            case "PENDING":
                return "Na čekanju";
            case "IN_PROGRESS":
                return "U obradi";
            case "COMPLETED":
                return "Završeno";
            case "REJECTED":
                return "Odbijeno";
            default:
                return status;
        }
    }

    private String formatDate(String date) {
        if (date != null && date.length() >= 10) {
            return date.substring(8, 10) + "." +
                    date.substring(5, 7) + "." +
                    date.substring(0, 4) + ".";
        }

        return "-";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRequestTitle;
        TextView txtVehicle;
        TextView txtDescription;
        TextView txtCreatedAt;
        TextView txtPriority;
        TextView txtRequestStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtRequestTitle = itemView.findViewById(R.id.txtRequestTitle);
            txtVehicle = itemView.findViewById(R.id.txtVehicle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            txtPriority = itemView.findViewById(R.id.txtPriority);
            txtRequestStatus = itemView.findViewById(R.id.txtRequestStatus);
        }
    }
    private void setStatusBackground(ViewHolder holder, String status) {
        if (status == null) {
            holder.txtRequestStatus.setBackgroundResource(R.drawable.bg_status_pending);
            return;
        }

        switch (status) {
            case "PENDING":
                holder.txtRequestStatus.setBackgroundResource(R.drawable.bg_status_pending);
                break;
            case "IN_PROGRESS":
                holder.txtRequestStatus.setBackgroundResource(R.drawable.bg_status_in_progress);
                break;
            case "COMPLETED":
                holder.txtRequestStatus.setBackgroundResource(R.drawable.bg_status_completed);
                break;
            case "REJECTED":
                holder.txtRequestStatus.setBackgroundResource(R.drawable.bg_status_rejected);
                break;
            default:
                holder.txtRequestStatus.setBackgroundResource(R.drawable.bg_status_pending);
                break;
        }
    }
}