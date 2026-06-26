package com.example.diplomskiandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.models.ServiceRequest;

import java.util.List;

public class ServiceRequestAdapter extends RecyclerView.Adapter<ServiceRequestAdapter.ViewHolder> {

    private final List<ServiceRequest> requests;

    public ServiceRequestAdapter(List<ServiceRequest> requests) {
        this.requests = requests;
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
                request.getServiceType() != null && !request.getServiceType().isEmpty()
                        ? request.getServiceType()
                        : "Servisni zahtjev"
        );

        holder.txtDescription.setText(request.getProblemDescription());

        holder.txtVehicle.setText(
                request.getVehicleName() + " • " + request.getLicensePlate()
        );

        holder.txtPriority.setText(
                request.getUrgency() != null ?  request.getUrgency() : "-"
        );

        holder.txtCreatedAt.setText("📅 " + formatDate(request.getCreatedAt()));

        holder.txtRequestStatus.setText(formatStatus(request.getStatus()));
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

    @Override
    public int getItemCount() {
        return requests.size();
    }

    private String formatDate(String date) {

        if (date != null && date.length() >= 10) {

            String year = date.substring(0, 4);
            String month = date.substring(5, 7);
            String day = date.substring(8, 10);

            return day + "." + month + "." + year + ".";
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
}