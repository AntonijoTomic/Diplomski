package com.example.diplomskiandroid.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.ServiceRequestApi;
import com.example.diplomskiandroid.api.VehicleApi;
import com.example.diplomskiandroid.models.ServiceRequest;
import com.example.diplomskiandroid.models.ServiceRequestCreateRequest;
import com.example.diplomskiandroid.models.Vehicle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.diplomskiandroid.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddServiceRequestActivity extends AppCompatActivity {
    private AutoCompleteTextView actVehicle;
    private AutoCompleteTextView actServiceType;
    private AutoCompleteTextView actUrgency;

    private VehicleApi vehicleApi;
    private TextInputEditText etDesiredDate;
    private TextInputEditText etProblemDescription;
    private TextInputEditText etNote;
    private MaterialButton btnSaveRequest;
    private ServiceRequestApi serviceRequestApi;

    private final List<Vehicle> vehicles = new ArrayList<>();

    private int selectedVehicleId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_service_request);

        View root = findViewById(R.id.mainScroll);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());

            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    Math.max(systemBars.bottom, ime.bottom)
            );
            return insets;
        });


        actVehicle = findViewById(R.id.actVehicle);
        actServiceType = findViewById(R.id.actServiceType);
        actUrgency = findViewById(R.id.actUrgency);

        etDesiredDate = findViewById(R.id.etDesiredDate);
        etProblemDescription = findViewById(R.id.etProblemDescription);
        etNote = findViewById(R.id.etNote);
        btnSaveRequest = findViewById(R.id.btnSaveRequest);

        btnSaveRequest.setOnClickListener(v -> saveServiceRequest());

        vehicleApi = ApiClient.getClient(this).create(VehicleApi.class);
        serviceRequestApi = ApiClient.getClient(this).create(ServiceRequestApi.class);

        etDesiredDate.setOnClickListener(v -> showDatePicker());
        etDesiredDate.setFocusable(false);
        etDesiredDate.setClickable(true);
        etDesiredDate.setCursorVisible(false);
        etDesiredDate.setKeyListener(null);

        loadVehicles();

        setupDropdowns();
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

                    etDesiredDate.setText(formattedDate);
                },
                year,
                month,
                day
        );
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void saveServiceRequest() {String problemDescription = etProblemDescription.getText().toString().trim();
        String serviceType = actServiceType.getText().toString().trim();
        String urgency = actUrgency.getText().toString().trim();
        String desiredDate = etDesiredDate.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (selectedVehicleId == 0) {
            Toast.makeText(this, "Odaberite vozilo.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (problemDescription.isEmpty()) {
            Toast.makeText(this, "Unesite opis problema.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (desiredDate.isEmpty()) {
            desiredDate = null;
        } else if (desiredDate.length() == 10) {
            desiredDate = desiredDate + "T00:00:00Z";
        }

        if (serviceType.isEmpty()) {
            Toast.makeText(this, "Odaberite vrstu servisa.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (urgency.isEmpty()) {
            Toast.makeText(this, "Odaberite hitnost zahtjeva.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences preferences = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", 0);

        ServiceRequestCreateRequest request = new ServiceRequestCreateRequest(
                userId,
                selectedVehicleId,
                problemDescription,
                serviceType,
                desiredDate,
                urgency,
                note
        );

        btnSaveRequest.setEnabled(false);

        serviceRequestApi.createServiceRequest(request).enqueue(new Callback<ServiceRequest>() {
            @Override
            public void onResponse(Call<ServiceRequest> call, Response<ServiceRequest> response) {
                btnSaveRequest.setEnabled(true);

                if (response.isSuccessful()) {
                    showSuccessDialog("Servisni zahtjev je uspješno poslan.");
                } else {
                    Toast.makeText(AddServiceRequestActivity.this,
                            "Greška kod slanja zahtjeva: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServiceRequest> call, Throwable t) {
                btnSaveRequest.setEnabled(true);

                Toast.makeText(AddServiceRequestActivity.this,
                        "Greška povezivanja: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private void showSuccessDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);

        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        TextView txtMessage = dialogView.findViewById(R.id.txtMessage);
        if (txtMessage != null) {
            txtMessage.setText(message);
        }

        dialogView.findViewById(R.id.btnOk).setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
    private void setupDropdowns() {

        String[] serviceTypes = {
                "Redovni servis",
                "Veliki servis",
                "Dijagnostika",
                "Kočioni sustav",
                "Motor",
                "Ovjes",
                "Elektronika",
                "Klima uređaj",
                "Gume i kotači",
                "Ostalo"
        };

         String[] urgency = {
                "Niska",
                "Srednja",
                "Visoka"
        };

        actServiceType.setAdapter(
                new ArrayAdapter<>(this, R.layout.item_dropdown, serviceTypes)
        );

        actUrgency.setAdapter(
                new ArrayAdapter<>(this, R.layout.item_dropdown, urgency)
        );

    }

    private void loadVehicles() {
        SharedPreferences preferences =
                getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);

        int userId = preferences.getInt("userId", 0);

        vehicleApi.getVehiclesByUser(userId).enqueue(new Callback<List<Vehicle>>() {

            @Override
            public void onResponse(Call<List<Vehicle>> call,
                                   Response<List<Vehicle>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    vehicles.clear();
                    vehicles.addAll(response.body());

                    List<String> names = new ArrayList<>();

                    for (Vehicle vehicle : vehicles) {
                        names.add(vehicle.getBrand() + " " + vehicle.getModel());
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    AddServiceRequestActivity.this,
                                    R.layout.item_dropdown,
                                    names);

                    actVehicle.setAdapter(adapter);

                    actVehicle.setOnItemClickListener((parent, view, position, id) -> {
                        selectedVehicleId = vehicles.get(position).getId();
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Vehicle>> call, Throwable t) {

            }
        });
    }
}