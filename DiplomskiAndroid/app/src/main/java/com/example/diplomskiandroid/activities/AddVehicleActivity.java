package com.example.diplomskiandroid.activities;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.VehicleApi;
import com.example.diplomskiandroid.models.Vehicle;
import com.example.diplomskiandroid.models.VehicleCreateRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVehicleActivity extends AppCompatActivity {

    private TextInputEditText etBrand, etModel, etYear, etMileage,
            etLicensePlate, etVin, etRegistrationDate, etNote;

    private AutoCompleteTextView actFuelType;
    private MaterialButton btnSaveVehicle;

    private VehicleApi vehicleApi;
    private int vehicleId = 0;
    private boolean isEditMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_add_vehicle);

        etBrand = findViewById(R.id.etBrand);
        etModel = findViewById(R.id.etModel);
        etYear = findViewById(R.id.etYear);
        etMileage = findViewById(R.id.etMileage);
        etLicensePlate = findViewById(R.id.etLicensePlate);
        etVin = findViewById(R.id.etVin);
        etRegistrationDate = findViewById(R.id.etRegistrationDate);
        etNote = findViewById(R.id.etNote);
        actFuelType = findViewById(R.id.actFuelType);
        btnSaveVehicle = findViewById(R.id.btnSaveVehicle);

        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            int navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            view.setPadding(0, 0, 0, Math.max(imeHeight, navBar));
            return insets;
        });

        vehicleApi = ApiClient.getClient(this).create(VehicleApi.class);

        setupFuelDropdown();
        etRegistrationDate.setOnClickListener(v -> showDatePicker());
        etRegistrationDate.setFocusable(false);
        vehicleId = getIntent().getIntExtra("vehicleId", 0);
        isEditMode = vehicleId != 0;

        if (isEditMode) {
            loadVehicleForEdit(vehicleId);
            btnSaveVehicle.setText("Spremi promjene");
        }
        btnSaveVehicle.setOnClickListener(v -> {
            if (isEditMode) {
                updateVehicle();
            } else {
                saveVehicle();
            }
        });
    }

    private void loadVehicleForEdit(int vehicleId) {
        vehicleApi.getVehicleById(vehicleId).enqueue(new Callback<Vehicle>() {
            @Override
            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Vehicle vehicle = response.body();

                    etBrand.setText(vehicle.getBrand());
                    etModel.setText(vehicle.getModel());
                    etYear.setText(String.valueOf(vehicle.getYear()));
                    etMileage.setText(String.valueOf(vehicle.getMileage()));
                    etLicensePlate.setText(vehicle.getLicensePlate());
                    etVin.setText(vehicle.getVin());
                    actFuelType.setText(vehicle.getFuelType(), false);
                    etNote.setText(vehicle.getNote());

                    if (vehicle.getRegistrationDate() != null &&
                            vehicle.getRegistrationDate().length() >= 10) {
                        etRegistrationDate.setText(
                                vehicle.getRegistrationDate().substring(0, 10)
                        );
                    }
                }
            }

            @Override
            public void onFailure(Call<Vehicle> call, Throwable t) {
                Toast.makeText(AddVehicleActivity.this,
                        "Greška povezivanja: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateVehicle() {
        String brand = etBrand.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String yearText = etYear.getText().toString().trim();
        String mileageText = etMileage.getText().toString().trim();
        String licensePlate = etLicensePlate.getText().toString().trim();
        String vin = etVin.getText().toString().trim();
        String fuelType = actFuelType.getText().toString().trim();
        String registrationDate = etRegistrationDate.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (brand.isEmpty() || model.isEmpty() || yearText.isEmpty()
                || mileageText.isEmpty() || licensePlate.isEmpty()
                || fuelType.isEmpty()) {
            Toast.makeText(this, "Popunite obavezna polja.", Toast.LENGTH_SHORT).show();
            return;
        }

        int year;
        int mileage;

        try {
            year = Integer.parseInt(yearText);
            mileage = Integer.parseInt(mileageText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Godište i kilometraža moraju biti brojevi.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (registrationDate.isEmpty()) {
            registrationDate = null;
        } else if (registrationDate.length() == 10) {
            registrationDate = registrationDate + "T00:00:00Z";
        }

        SharedPreferences preferences = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        int userId = preferences.getInt("userId", 0);

        VehicleCreateRequest request = new VehicleCreateRequest(
                userId,
                brand,
                model,
                year,
                licensePlate,
                vin,
                fuelType,
                mileage,
                registrationDate,
                note
        );

        btnSaveVehicle.setEnabled(false);

        vehicleApi.updateVehicle(vehicleId, request).enqueue(new Callback<Vehicle>() {
            @Override
            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                btnSaveVehicle.setEnabled(true);

                if (response.isSuccessful()) {
                    showSuccessDialog();
                } else {
                    Toast.makeText(AddVehicleActivity.this,
                            "Greška kod ažuriranja vozila: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Vehicle> call, Throwable t) {
                btnSaveVehicle.setEnabled(true);

                Toast.makeText(AddVehicleActivity.this,
                        "Greška povezivanja: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialogView.findViewById(R.id.btnOk).setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
        dialog.show();
    }
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format(
                            "%04d-%02d-%02d",
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay
                    );

                    etRegistrationDate.setText(formattedDate);
                },
                year,
                month,
                day
        );

        long todayMillis = System.currentTimeMillis();
        long oneYearAgoMillis = todayMillis - TimeUnit.DAYS.toMillis(365);

        dialog.getDatePicker().setMaxDate(todayMillis);
        dialog.getDatePicker().setMinDate(oneYearAgoMillis);

        dialog.show();
    }

    private void setupFuelDropdown() {
        String[] fuelTypes = {"Benzin", "Diesel", "Hibrid", "Električno", "Plin"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                fuelTypes
        );
        actFuelType.setAdapter(adapter);

        actFuelType.setOnClickListener(v -> actFuelType.showDropDown());
    }

    private void saveVehicle() {
        String brand = etBrand.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String yearText = etYear.getText().toString().trim();
        String mileageText = etMileage.getText().toString().trim();
        String licensePlate = etLicensePlate.getText().toString().trim();
        String vin = etVin.getText().toString().trim();
        String fuelType = actFuelType.getText().toString().trim();
        String registrationDate = etRegistrationDate.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (brand.isEmpty() || model.isEmpty() || yearText.isEmpty()
                || mileageText.isEmpty() || licensePlate.isEmpty()
                || fuelType.isEmpty()) {

            Toast.makeText(this, "Popunite obavezna polja.", Toast.LENGTH_SHORT).show();
            return;
        }

        int year;
        int mileage;

        try {
            year = Integer.parseInt(yearText);
            mileage = Integer.parseInt(mileageText);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

            if (year < 1950 || year > currentYear + 1) {
                Toast.makeText(this, "Unesite ispravno godište vozila.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mileage < 0) {
                Toast.makeText(this, "Kilometraža ne može biti negativna.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Godište i kilometraža moraju biti brojevi.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (registrationDate.isEmpty()) {
            registrationDate = null;
        } else if (registrationDate.length() == 10) {
            registrationDate = registrationDate + "T00:00:00Z";
        }

        SharedPreferences preferences = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        int userId = preferences.getInt("userId", 0);

        VehicleCreateRequest request = new VehicleCreateRequest(
                userId,
                brand,
                model,
                year,
                licensePlate,
                vin,
                fuelType,
                mileage,
                registrationDate,
                note
        );

        btnSaveVehicle.setEnabled(false);

        
        vehicleApi.createVehicle(request).enqueue(new Callback<Vehicle>() {
            @Override
            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                btnSaveVehicle.setEnabled(true);

                if (response.isSuccessful()) {
                    showSuccessDialog();
                } else {
                    Toast.makeText(AddVehicleActivity.this,
                            response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Vehicle> call, Throwable t) {
                btnSaveVehicle.setEnabled(true);

                Toast.makeText(AddVehicleActivity.this,
                        "Greška povezivanja: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}