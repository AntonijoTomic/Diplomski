package com.example.diplomskiandroid.adapters;

import android.app.AlertDialog;
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
import com.example.diplomskiandroid.activities.AddVehicleActivity;
import com.example.diplomskiandroid.activities.VehicleDetailsActivity;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.VehicleApi;
import com.example.diplomskiandroid.models.Vehicle;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {
    public interface OnVehicleChangedListener {
        void onVehicleChanged();
    }
    private final List<Vehicle> vehicles;
    private final OnVehicleChangedListener listener;
    public VehicleAdapter(List<Vehicle> vehicles, OnVehicleChangedListener listener) {
        this.vehicles = vehicles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehicle, parent, false);

        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {

        Vehicle vehicle = vehicles.get(position);

        holder.txtVehicleName.setText(
                vehicle.getBrand() + " " + vehicle.getModel()
        );

        holder.txtVehiclePlate.setText(
                vehicle.getLicensePlate()
        );

        holder.txtVehicleInfo.setText(
                vehicle.getYear() + " • " +
                        vehicle.getFuelType() + " • " +
                        vehicle.getMileage() + " km"
        );

        holder.txtFuelType.setText(
                vehicle.getFuelType()
        );

        holder.txtMileage.setText(
                vehicle.getMileage() + " km"
        );

        holder.txtVehicleYear.setText(
                String.valueOf(vehicle.getYear())
        );
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), VehicleDetailsActivity.class);
            intent.putExtra("vehicleId", vehicle.getId());
            v.getContext().startActivity(intent);
        });
        holder.itemView.setOnLongClickListener(v -> {
            showVehicleOptions(v, vehicle);
            return true;
        });
    }

    private void showVehicleOptions(View view, Vehicle vehicle) {
        BottomSheetDialog dialog = new BottomSheetDialog(view.getContext());

        View sheetView = LayoutInflater.from(view.getContext())
                .inflate(R.layout.bottom_sheet_vehicle, null);

        LinearLayout layoutEditVehicle = sheetView.findViewById(R.id.layoutEditVehicle);
        LinearLayout layoutDeleteVehicle = sheetView.findViewById(R.id.layoutDeleteVehicle);

        layoutEditVehicle.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), AddVehicleActivity.class);
            intent.putExtra("vehicleId", vehicle.getId());
            view.getContext().startActivity(intent);
            dialog.dismiss();
        });

        layoutDeleteVehicle.setOnClickListener(v -> {

            dialog.dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

            View dialogView = LayoutInflater.from(view.getContext())
                    .inflate(R.layout.dialog_delete_vehicle, null);

            builder.setView(dialogView);
            builder.setCancelable(true);

            AlertDialog alertDialog = builder.create();

            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            TextView txtDeleteMessage = dialogView.findViewById(R.id.txtDeleteMessage);

            txtDeleteMessage.setText(
                    "Jeste li sigurni da želite obrisati vozilo "
                            + vehicle.getBrand() + " " + vehicle.getModel() + "?"
            );

            MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelDelete);
            MaterialButton btnDelete = dialogView.findViewById(R.id.btnConfirmDelete);

            btnCancel.setOnClickListener(v1 -> alertDialog.dismiss());

            btnDelete.setOnClickListener(v1 -> {
                alertDialog.dismiss();
                deleteVehicle(view, vehicle.getId());
            });

            alertDialog.show();
        });

        dialog.setContentView(sheetView);
        dialog.show();
    }

    private void deleteVehicle(View view, int id) {
        VehicleApi vehicleApi = ApiClient.getClient(view.getContext()).create(VehicleApi.class);

        vehicleApi.deleteVehicle(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(view.getContext(), "Vozilo je uspješno obrisano.", Toast.LENGTH_SHORT).show();

                    if (listener != null) {
                        listener.onVehicleChanged();
                    }
                } else {
                    Toast.makeText(view.getContext(), "Greška kod brisanja vozila: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(view.getContext(), "Greška povezivanja: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    static class VehicleViewHolder extends RecyclerView.ViewHolder {

        TextView txtVehicleName;
        TextView txtVehiclePlate;
        TextView txtVehicleInfo;
        TextView txtFuelType;
        TextView txtMileage;
        TextView txtVehicleYear;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);

            txtVehicleName = itemView.findViewById(R.id.txtVehicleName);
            txtVehiclePlate = itemView.findViewById(R.id.txtVehiclePlate);
            txtVehicleInfo = itemView.findViewById(R.id.txtVehicleInfo);
            txtFuelType = itemView.findViewById(R.id.txtFuelType);
            txtMileage = itemView.findViewById(R.id.txtMileage);
            txtVehicleYear = itemView.findViewById(R.id.txtVehicleYear);
        }
    }
}