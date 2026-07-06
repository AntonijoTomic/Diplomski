package com.example.diplomskiandroid.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.activities.AddServiceRequestActivity;
import com.example.diplomskiandroid.activities.ServiceRequestDetailsActivity;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.ServiceRequestApi;
import com.example.diplomskiandroid.models.ServiceRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceRequestAdapter extends RecyclerView.Adapter<ServiceRequestAdapter.ViewHolder> {

    private final List<ServiceRequest> requests;
    private final ServiceRequestApi serviceRequestApi;

    public ServiceRequestAdapter(List<ServiceRequest> requests, Context context) {
        this.requests = requests;
        this.serviceRequestApi = ApiClient.getClient(context).create(ServiceRequestApi.class);
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
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ServiceRequestDetailsActivity.class);
            intent.putExtra("requestId", request.getId());
            v.getContext().startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ServiceRequestDetailsActivity.class);
            intent.putExtra("requestId", request.getId());
            v.getContext().startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            showRequestOptions(v, request);
            return true;
        });
    }

    private void showRequestOptions(View view, ServiceRequest request) {

        if (!"PENDING".equals(request.getStatus())) {
            Toast.makeText(
                    view.getContext(),
                    "Zahtjev više nije moguće uređivati ili obrisati.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        BottomSheetDialog dialog = new BottomSheetDialog(view.getContext());

        View sheetView = LayoutInflater.from(view.getContext())
                .inflate(R.layout.bottom_sheet_service_request, null);

        LinearLayout layoutEditRequest = sheetView.findViewById(R.id.layoutEditRequest);
        LinearLayout layoutDeleteRequest = sheetView.findViewById(R.id.layoutDeleteRequest);

        layoutEditRequest.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), AddServiceRequestActivity.class);
            intent.putExtra("requestId", request.getId());
            view.getContext().startActivity(intent);
            dialog.dismiss();
        });

        layoutDeleteRequest.setOnClickListener(v -> {
            dialog.dismiss();
            showDeleteDialog(view, request);
        });

        dialog.setContentView(sheetView);
        dialog.show();
    }

    private void showDeleteDialog(View view, ServiceRequest request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        View dialogView = LayoutInflater.from(view.getContext())
                .inflate(R.layout.dialog_delete_vehicle, null);

        builder.setView(dialogView);
        builder.setCancelable(true);

        AlertDialog alertDialog = builder.create();

        TextView txtDeleteTitle = dialogView.findViewById(R.id.txtDeleteTitle);
        TextView txtDeleteMessage = dialogView.findViewById(R.id.txtDeleteMessage);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelDelete);
        MaterialButton btnDelete = dialogView.findViewById(R.id.btnConfirmDelete);

        txtDeleteTitle.setText("Obrisati servisni zahtjev?");
        txtDeleteMessage.setText("Jeste li sigurni da želite obrisati ovaj servisni zahtjev?");

        btnCancel.setOnClickListener(v -> alertDialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            alertDialog.dismiss();
            deleteRequest(view, request.getId());
        });

        alertDialog.show();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void deleteRequest(View view, int id) {
        serviceRequestApi.deleteRequest(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    int position = -1;

                    for (int i = 0; i < requests.size(); i++) {
                        if (requests.get(i).getId() == id) {
                            position = i;
                            break;
                        }
                    }

                    if (position != -1) {
                        requests.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, requests.size());
                    }

                    Toast.makeText(view.getContext(),
                            "Servisni zahtjev je obrisan.",
                            Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(view.getContext(),
                            "Greška kod brisanja.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(view.getContext(),
                        "Greška povezivanja.",
                        Toast.LENGTH_SHORT).show();
            }
        });
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