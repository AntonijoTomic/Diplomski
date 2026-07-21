package com.example.diplomskiandroid.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.activities.VehicleDetailsActivity;
import com.example.diplomskiandroid.models.Vehicle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VehicleSummaryAdapter extends RecyclerView.Adapter<VehicleSummaryAdapter.VehicleViewHolder> {

    private final List<Vehicle> vehicles;


    public VehicleSummaryAdapter(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dashboard_vehicle, parent, false);

        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {

        Vehicle vehicle = vehicles.get(position);

        holder.txtVehicleName.setText(vehicle.getBrand() + " " + vehicle.getModel());
        holder.txtLicensePlate.setText(vehicle.getLicensePlate());
        holder.txtMileage.setText(vehicle.getMileage() + " km");

        holder.txtRegistration.setText(
                calculateRemainingDays(vehicle.getRegistrationDate())
        );

        holder.txtNextService.setText(
                calculateNextService(vehicle.getLastServiceDate())
        );
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), VehicleDetailsActivity.class);
            intent.putExtra("vehicleId", vehicle.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    static class VehicleViewHolder extends RecyclerView.ViewHolder {

        TextView txtVehicleName;
        TextView txtLicensePlate;
        TextView txtRegistration;
        TextView txtNextService;
        TextView txtMileage;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);

            txtVehicleName = itemView.findViewById(R.id.txtVehicleName);
            txtLicensePlate = itemView.findViewById(R.id.txtLicensePlate);
            txtRegistration = itemView.findViewById(R.id.txtRegistration);
            txtNextService = itemView.findViewById(R.id.txtNextService);
            txtMileage = itemView.findViewById(R.id.txtMileage);
        }
    }

    private String calculateRemainingDays(String registrationDate) {

        if (registrationDate == null)
            return "Nepoznato";

        try {
            SimpleDateFormat format =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

            Date lastRegistration = format.parse(registrationDate);

            long expiryMillis =
                    lastRegistration.getTime() + TimeUnit.DAYS.toMillis(365);

            long diff = expiryMillis - System.currentTimeMillis();

            long days = TimeUnit.MILLISECONDS.toDays(diff);

            if (days < 0)
                return "Istekla";

            return days + " dana";

        } catch (Exception e) {
            return "-";
        }
    }

    private String calculateNextService(String lastServiceDate) {

        if (lastServiceDate == null)
            return "Nema podataka";

        try {

            SimpleDateFormat format =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

            Date lastService = format.parse(lastServiceDate);

            long nextService =
                    lastService.getTime() + TimeUnit.DAYS.toMillis(365);

            long diff = nextService - System.currentTimeMillis();

            long days = TimeUnit.MILLISECONDS.toDays(diff);

            if (days < 0)
                return "Servis potreban";

            return days + " dana";

        } catch (Exception e) {
            return "-";
        }
    }
}