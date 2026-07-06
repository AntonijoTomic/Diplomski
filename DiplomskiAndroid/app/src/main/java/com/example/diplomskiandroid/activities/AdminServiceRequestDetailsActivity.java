package com.example.diplomskiandroid.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.ServiceRequestApi;
import com.example.diplomskiandroid.models.ServiceRequest;
import com.example.diplomskiandroid.models.Vehicle;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminServiceRequestDetailsActivity extends AppCompatActivity {
    private LinearLayout layoutActions;
    private LinearLayout layoutWorkOrder;

    private MaterialCardView cardStatusInfo;
    private TextView txtStatusInfo;

    private MaterialButton btnCreateWorkOrder;
    private TextView txtBack;

    private TextView txtVehicleName;
    private TextView txtLicensePlate;
    private TextView txtStatus;

    private TextView txtOwnerName;
    private TextView txtOwnerEmail;
    private TextView txtOwnerPhone;

    private TextView txtYear;
    private TextView txtFuelType;
    private TextView txtMileage;
    private TextView txtRegistrationDate;
    private TextView txtVin;

    private TextView txtProblemDescription;
    private TextView txtNote;

    private TextView txtServiceType;
    private TextView txtUrgency;
    private TextView txtDesiredDate;
    private TextView txtCreatedAt;

    private CardView cardNote;

    private MaterialButton btnAccept;
    private MaterialButton btnReject;

    private ServiceRequestApi serviceRequestApi;

    private int requestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_service_request_details);

        requestId = getIntent().getIntExtra("requestId", 0);

        txtBack = findViewById(R.id.txtBack);
        layoutActions = findViewById(R.id.buttons);
        layoutWorkOrder = findViewById(R.id.layoutWorkOrder);

        cardStatusInfo = findViewById(R.id.cardStatusInfo);
        txtStatusInfo = findViewById(R.id.txtStatusInfo);

        btnCreateWorkOrder = findViewById(R.id.btnCreateWorkOrder);
        txtVehicleName = findViewById(R.id.txtVehicleName);
        txtLicensePlate = findViewById(R.id.txtLicensePlate);
        txtStatus = findViewById(R.id.txtStatus);

        txtOwnerName = findViewById(R.id.txtOwnerName);
        txtOwnerEmail = findViewById(R.id.txtOwnerEmail);
        txtOwnerPhone = findViewById(R.id.txtOwnerPhone);

        txtYear = findViewById(R.id.txtYear);
        txtFuelType = findViewById(R.id.txtFuelType);
        txtMileage = findViewById(R.id.txtMileage);
        txtRegistrationDate = findViewById(R.id.txtRegistrationDate);
        txtVin = findViewById(R.id.txtVin);

        txtProblemDescription = findViewById(R.id.txtProblemDescription);
        txtNote = findViewById(R.id.txtNote);

        txtServiceType = findViewById(R.id.txtServiceType);
        txtUrgency = findViewById(R.id.txtUrgency);
        txtDesiredDate = findViewById(R.id.txtDesiredDate);
        txtCreatedAt = findViewById(R.id.txtCreatedAt);

        cardNote = findViewById(R.id.cardNote);

        btnAccept = findViewById(R.id.btnAccept);
        btnReject = findViewById(R.id.btnReject);

        serviceRequestApi =
                ApiClient.getClient(this).create(ServiceRequestApi.class);

        txtBack.setOnClickListener(v -> finish());
        btnAccept.setOnClickListener(v -> updateStatus("IN_PROGRESS"));
        btnReject.setOnClickListener(v -> updateStatus("REJECTED"));


        loadRequest();
    }

    private void loadRequest() {

        serviceRequestApi.getRequestById(requestId).enqueue(new Callback<ServiceRequest>() {

            @Override
            public void onResponse(Call<ServiceRequest> call,
                                   Response<ServiceRequest> response) {

                if (response.isSuccessful() && response.body() != null) {

                    showRequest(response.body());

                } else {

                    finish();
                }
            }

            @Override
            public void onFailure(Call<ServiceRequest> call, Throwable t) {
                finish();
            }
        });
    }
    private void showRequest(ServiceRequest request) {

        Vehicle vehicle = request.getVehicle();

        if (vehicle != null) {

            txtVehicleName.setText(vehicle.getBrand() + " " + vehicle.getModel());
            txtLicensePlate.setText(vehicle.getLicensePlate());
            txtYear.setText(String.valueOf(vehicle.getYear()));
            txtFuelType.setText(vehicle.getFuelType());

            txtMileage.setText(
                    NumberFormat.getInstance(new Locale("hr", "HR"))
                            .format(vehicle.getMileage()) + " km"
            );

            txtRegistrationDate.setText(formatDate(vehicle.getRegistrationDate()));
            txtVin.setText(vehicle.getVin());
        }

        txtStatus.setText(formatStatus(request.getStatus()));
         setStatusBackground(request.getStatus());
        updateActionLayout(request.getStatus());


        txtOwnerName.setText(
                request.getUser().getFirstName() + " " +
                        request.getUser().getLastName()
        );

        txtOwnerEmail.setText(request.getUser().getEmail());
        txtOwnerPhone.setText(request.getUser().getPhoneNumber());

        txtProblemDescription.setText(request.getProblemDescription());

        if (request.getNote() == null || request.getNote().trim().isEmpty()) {

            cardNote.setVisibility(View.GONE);

        } else {

            txtNote.setText(request.getNote());
        }

        txtServiceType.setText(request.getServiceType());
        txtUrgency.setText(request.getUrgency());

        txtDesiredDate.setText(formatDate(request.getDesiredDate()));
        txtCreatedAt.setText(formatDate(request.getCreatedAt()));
    }

    private void updateActionLayout(String status) {

        layoutActions.setVisibility(View.GONE);
        layoutWorkOrder.setVisibility(View.GONE);
        cardStatusInfo.setVisibility(View.GONE);

        switch (status) {

            case "PENDING":
                layoutActions.setVisibility(View.VISIBLE);
                break;

            case "IN_PROGRESS":
                layoutWorkOrder.setVisibility(View.VISIBLE);
                break;

            case "COMPLETED":

                cardStatusInfo.setVisibility(View.VISIBLE);
                txtStatusInfo.setText("Servis je uspješno završen.");

                break;

            case "REJECTED":

                cardStatusInfo.setVisibility(View.VISIBLE);
                txtStatusInfo.setText("Servisni zahtjev je odbijen.");

                break;
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
        }
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
    private String formatDate(String date) {

        if (date == null || date.length() < 10)
            return "-";

        return date.substring(8,10) + "." +
                date.substring(5,7) + "." +
                date.substring(0,4) + ".";
    }
    private void updateStatus(String status) {
        serviceRequestApi.updateStatus(requestId, status)
                .enqueue(new Callback<ServiceRequest>() {
                    @Override
                    public void onResponse(Call<ServiceRequest> call, Response<ServiceRequest> response) {
                        if (response.isSuccessful()) {
                            if ("IN_PROGRESS".equals(status)) {
                                showSuccessDialog("Servisni zahtjev je uspješno prihvaćen.");
                            } else if ("REJECTED".equals(status)) {
                                showSuccessDialog("Servisni zahtjev je uspješno odbijen.");
                            } else {
                                loadRequest();
                            }
                        } else {
                            Toast.makeText(AdminServiceRequestDetailsActivity.this,
                                    "Greška kod promjene statusa.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ServiceRequest> call, Throwable t) {
                        Toast.makeText(AdminServiceRequestDetailsActivity.this,
                                "Greška povezivanja.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showSuccessDialog(String message) {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_success);
        dialog.setCancelable(false);

        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        MaterialButton btnOk = dialog.findViewById(R.id.btnOk);

        txtMessage.setText(message);

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            loadRequest();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }
}