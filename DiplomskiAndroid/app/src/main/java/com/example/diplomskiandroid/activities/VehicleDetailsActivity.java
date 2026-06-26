package com.example.diplomskiandroid.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.VehicleApi;
import com.example.diplomskiandroid.models.Vehicle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleDetailsActivity extends AppCompatActivity {

    private TextView txtBack;
    private TextView txtVehicleName;
    private TextView txtVehiclePlate;
    private TextView txtYear;
    private TextView txtFuelType;
    private TextView txtMileage;
    private TextView txtRegistrationDate;
    private TextView txtVin;
    private TextView txtNote;

    private VehicleApi vehicleApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        txtBack = findViewById(R.id.txtBack);
        txtVehicleName = findViewById(R.id.txtVehicleName);
        txtVehiclePlate = findViewById(R.id.txtVehiclePlate);
        txtYear = findViewById(R.id.txtYear);
        txtFuelType = findViewById(R.id.txtFuelType);
        txtMileage = findViewById(R.id.txtMileage);
        txtRegistrationDate = findViewById(R.id.txtRegistrationDate);
        txtVin = findViewById(R.id.txtVin);
        txtNote = findViewById(R.id.txtNote);

        txtBack.setOnClickListener(v -> finish());

        vehicleApi = ApiClient.getClient(this).create(VehicleApi.class);

        int vehicleId = getIntent().getIntExtra("vehicleId", 0);

        if (vehicleId == 0) {
            Toast.makeText(this, "Vozilo nije pronađeno.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadVehicle(vehicleId);
    }

    private void loadVehicle(int vehicleId) {
        vehicleApi.getVehicleById(vehicleId).enqueue(new Callback<Vehicle>() {
            @Override
            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showVehicle(response.body());
                } else {
                    Toast.makeText(VehicleDetailsActivity.this,
                            "Greška kod dohvaćanja vozila.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Vehicle> call, Throwable t) {
                Toast.makeText(VehicleDetailsActivity.this,
                        "Greška povezivanja: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showVehicle(Vehicle vehicle) {
        txtVehicleName.setText(vehicle.getBrand() + " " + vehicle.getModel());
        txtVehiclePlate.setText(vehicle.getLicensePlate());

        txtYear.setText(vehicle.getYear() + "\nGodište");
        txtFuelType.setText(vehicle.getFuelType() + "\nGorivo");
        txtMileage.setText(vehicle.getMileage() + " km\nKilometraža");

        String registrationDate = vehicle.getRegistrationDate();

        if (registrationDate == null || registrationDate.isEmpty()) {
            txtRegistrationDate.setText("-\nRegistracija");
        } else {
            txtRegistrationDate.setText(formatDate(registrationDate) + "\nRegistracija");
        }

        if (vehicle.getVin() == null || vehicle.getVin().isEmpty()) {
            txtVin.setText("VIN\n-");
        } else {
            txtVin.setText("VIN\n" + vehicle.getVin());
        }

        if (vehicle.getNote() == null || vehicle.getNote().isEmpty()) {
            txtNote.setText("Napomena\n-");
        } else {
            txtNote.setText("Napomena\n" + vehicle.getNote());
        }
    }

    private String formatDate(String date) {
        if (date.length() >= 10) {
            String year = date.substring(0, 4);
            String month = date.substring(5, 7);
            String day = date.substring(8, 10);

            return day + "." + month + "." + year + ".";
        }

        return date;
    }
}