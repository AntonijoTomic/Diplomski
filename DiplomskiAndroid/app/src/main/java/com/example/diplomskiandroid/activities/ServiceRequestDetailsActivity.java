package com.example.diplomskiandroid.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.ServiceRequestApi;
import com.example.diplomskiandroid.models.ServiceRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceRequestDetailsActivity extends AppCompatActivity {

    private TextView txtBack, txtStatus, txtVehicle, txtServiceType,
            txtUrgency, txtProblemDescription, txtNote, txtCreatedAt, txtDesiredDate;

    private ServiceRequestApi serviceRequestApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request_details);

        txtBack = findViewById(R.id.txtBack);
        txtStatus = findViewById(R.id.txtStatus);
        txtVehicle = findViewById(R.id.txtVehicle);
        txtServiceType = findViewById(R.id.txtServiceType);
        txtUrgency = findViewById(R.id.txtUrgency);
        txtProblemDescription = findViewById(R.id.txtProblemDescription);
        txtNote = findViewById(R.id.txtNote);
        txtCreatedAt = findViewById(R.id.txtCreatedAt);
        txtDesiredDate = findViewById(R.id.txtDesiredDate);
        txtBack.setOnClickListener(v -> finish());

        serviceRequestApi = ApiClient.getClient(this).create(ServiceRequestApi.class);

        int requestId = getIntent().getIntExtra("requestId", 0);

        if (requestId == 0) {
            Toast.makeText(this, "Zahtjev nije pronađen.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRequest(requestId);
    }

    private void loadRequest(int requestId) {
        serviceRequestApi.getRequestById(requestId).enqueue(new Callback<ServiceRequest>() {
            @Override
            public void onResponse(Call<ServiceRequest> call, Response<ServiceRequest> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showRequest(response.body());
                } else {
                    Toast.makeText(ServiceRequestDetailsActivity.this,
                            "Greška kod dohvaćanja zahtjeva: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServiceRequest> call, Throwable t) {
                Toast.makeText(ServiceRequestDetailsActivity.this,
                        "Greška povezivanja: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showRequest(ServiceRequest request) {
        txtStatus.setText(formatStatus(request.getStatus()));
        setStatusBackground(request.getStatus());
        txtVehicle.setText(
                safe(request.getVehicleName()) + "\n" + safe(request.getLicensePlate())
        );

        txtServiceType.setText(safe(request.getServiceType()));
        txtUrgency.setText(safe(request.getUrgency()));
        txtProblemDescription.setText(safe(request.getProblemDescription()));
        txtCreatedAt.setText(formatDate(request.getCreatedAt()));
        txtDesiredDate.setText(formatDate(request.getDesiredDate()));
        if (request.getNote() == null || request.getNote().isEmpty()) {
            txtNote.setText("-");
        } else {
            txtNote.setText(request.getNote());
        }
    }

    private void setStatusBackground(String status) {
        if (status == null) {
            txtStatus.setBackgroundResource(R.drawable.bg_status_pending);
            return;
        }

        switch (status) {
            case "PENDING":
                txtStatus.setBackgroundResource(R.drawable.bg_status_pending);
                break;
            case "IN_PROGRESS":
                txtStatus.setBackgroundResource(R.drawable.bg_status_in_progress);
                break;
            case "COMPLETED":
                txtStatus.setBackgroundResource(R.drawable.bg_status_completed);
                break;
            case "REJECTED":
                txtStatus.setBackgroundResource(R.drawable.bg_status_rejected);
                break;
            default:
                txtStatus.setBackgroundResource(R.drawable.bg_status_pending);
                break;
    }}

    private String safe(String value) {
        return value == null || value.isEmpty() ? "-" : value;
    }

    private String formatStatus(String status) {
        if (status == null) return "-";

        switch (status) {
            case "PENDING":
                return "NA ČEKANJU";
            case "IN_PROGRESS":
                return "U OBRADI";
            case "COMPLETED":
                return "ZAVRŠENO";
            case "REJECTED":
                return "ODBIJENO";
            default:
                return status;
        }
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
}