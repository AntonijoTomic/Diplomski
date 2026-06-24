package com.example.diplomskiandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.models.Vehicle;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private final List<Vehicle> vehicles;

    public VehicleAdapter(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
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