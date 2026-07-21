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
    private TextInputEditText etCurrentMileage;
    private VehicleApi vehicleApi;
    private TextInputEditText etDesiredDate;
    private TextInputEditText etProblemDescription;
    private TextInputEditText etNote;
    private MaterialButton btnSaveRequest;
    private ServiceRequestApi serviceRequestApi;

    private final List<Vehicle> vehicles = new ArrayList<>();

    private int selectedVehicleId = 0;
    private int requestId = 0;
    private int editVehicleId = 0;
    private boolean isEditMode = false;
    private TextView txtAddRequestTitle;
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
        etCurrentMileage = findViewById(R.id.etCurrentMileage);

        btnSaveRequest.setOnClickListener(v -> {
            if (isEditMode) {
                updateServiceRequest();
            } else {
                saveServiceRequest();
            }
        });

        vehicleApi = ApiClient.getClient(this).create(VehicleApi.class);
        serviceRequestApi = ApiClient.getClient(this).create(ServiceRequestApi.class);

        etDesiredDate.setOnClickListener(v -> showDatePicker());
        etDesiredDate.setFocusable(false);
        etDesiredDate.setClickable(true);
        etDesiredDate.setCursorVisible(false);
        etDesiredDate.setKeyListener(null);
        requestId = getIntent().getIntExtra("requestId", 0);
        isEditMode = requestId != 0;
        txtAddRequestTitle = findViewById(R.id.txtAddRequestTitle);

        if (isEditMode) {
            txtAddRequestTitle.setText("Uredi servisni zahtjev");
            btnSaveRequest.setText("Spremi promjene");
            loadRequestForEdit(requestId);
        }


        setupDropdowns();
    }

    private void updateServiceRequest() {
        String problemDescription = etProblemDescription.getText().toString().trim();
        String serviceType = actServiceType.getText().toString().trim();
        String urgency = actUrgency.getText().toString().trim();
        String desiredDate = etDesiredDate.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        String mileageText = etCurrentMileage.getText().toString().trim();

        if (mileageText.isEmpty()) {
            Toast.makeText(this,
                    "Unesite trenutnu kilometražu.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int currentMileage = Integer.parseInt(mileageText);

        SharedPreferences preferences =
                getSharedPreferences("USER_SESSION", MODE_PRIVATE);

        int userId = preferences.getInt("userId", 0);

        if (desiredDate.isEmpty()) {
            desiredDate = null;
        } else if (desiredDate.length() == 10) {
            desiredDate += "T00:00:00Z";
        }

        ServiceRequestCreateRequest request =
                new ServiceRequestCreateRequest(
                        userId,
                        selectedVehicleId,
                        problemDescription,
                        serviceType,
                        desiredDate,
                        urgency,
                        note,
                        currentMileage
                );

        serviceRequestApi.updateServiceRequest(requestId, request)
                .enqueue(new Callback<ServiceRequest>() {

                    @Override
                    public void onResponse(Call<ServiceRequest> call,
                                           Response<ServiceRequest> response) {

                        if (response.isSuccessful()) {

                            showSuccessDialog(
                                    "Servisni zahtjev je uspješno ažuriran."
                            );

                        }  else {
                        String errorMessage = "Došlo je do greške.";

                        try {
                            if (response.errorBody() != null) {
                                errorMessage = response.errorBody().string();
                                errorMessage = errorMessage.replace("\"", "");
                            }
                        } catch (Exception e) {
                            errorMessage = "Greška kod ažuriranja.";
                        }

                        Toast.makeText(
                                AddServiceRequestActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                    }

                    @Override
                    public void onFailure(Call<ServiceRequest> call,
                                          Throwable t) {

                        Toast.makeText(
                                AddServiceRequestActivity.this,
                                "Greška povezivanja.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void loadRequestForEdit(int requestId) {

        serviceRequestApi.getRequestById(requestId).enqueue(new Callback<ServiceRequest>() {

            @Override
            public void onResponse(Call<ServiceRequest> call,
                                   Response<ServiceRequest> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ServiceRequest request = response.body();


                    actServiceType.setText(request.getServiceType(), false);
                    actUrgency.setText(request.getUrgency(), false);

                    etProblemDescription.setText(request.getProblemDescription());
                    etNote.setText(request.getNote());
                    etCurrentMileage.setText(
                            String.valueOf(request.getVehicle().getMileage())
                    );

                    if (request.getDesiredDate() != null &&
                            request.getDesiredDate().length() >= 10) {

                        etDesiredDate.setText(
                                request.getDesiredDate().substring(0, 10)
                        );
                    }

                    selectedVehicleId = request.getVehicleId();
                    editVehicleId = request.getVehicleId();

                    loadVehicles();

                } else {

                    Toast.makeText(AddServiceRequestActivity.this,
                            "Greška kod dohvaćanja zahtjeva.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServiceRequest> call, Throwable t) {

                Toast.makeText(AddServiceRequestActivity.this,
                        "Greška povezivanja.",
                        Toast.LENGTH_SHORT).show();
            }
        });
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
        String mileageText = etCurrentMileage.getText().toString().trim();

        if (mileageText.isEmpty()) {
            Toast.makeText(this,
                    "Unesite trenutnu kilometražu.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int currentMileage = Integer.parseInt(mileageText);

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
                note,
                currentMileage);

        btnSaveRequest.setEnabled(false);

        serviceRequestApi.createServiceRequest(request).enqueue(new Callback<ServiceRequest>() {
            @Override
            public void onResponse(Call<ServiceRequest> call, Response<ServiceRequest> response) {
                btnSaveRequest.setEnabled(true);

                if (response.isSuccessful()) {
                    showSuccessDialog("Servisni zahtjev je uspješno poslan.");
                }  else {
                    String errorMessage = "Došlo je do greške.";

                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();

                            errorMessage = errorMessage.replace("\"", "");
                        }
                    } catch (Exception e) {
                        errorMessage = "Greška kod slanja zahtjeva.";
                    }

                    Toast.makeText(
                            AddServiceRequestActivity.this,
                            errorMessage,
                            Toast.LENGTH_LONG
                    ).show();
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
                    if (isEditMode) {
                        for (Vehicle vehicle : vehicles) {
                            if (vehicle.getId() == editVehicleId) {
                                actVehicle.setText(
                                        vehicle.getBrand() + " " + vehicle.getModel(),
                                        false
                                );
                                break;
                            }
                        }
                    }
                    actVehicle.setOnItemClickListener((parent, view, position, id) -> {

                        Vehicle selectedVehicle = vehicles.get(position);

                        selectedVehicleId = selectedVehicle.getId();

                        etCurrentMileage.setText(
                                String.valueOf(selectedVehicle.getMileage())
                        );
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Vehicle>> call, Throwable t) {

            }
        });
    }
}