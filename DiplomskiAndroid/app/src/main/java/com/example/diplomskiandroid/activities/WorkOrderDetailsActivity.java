package com.example.diplomskiandroid.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;
import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.adapters.WorkOrderPartAdapter;
import com.example.diplomskiandroid.adapters.WorkOrderServiceAdapter;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.PartApi;
import com.example.diplomskiandroid.api.ServiceApi;
import com.example.diplomskiandroid.api.WorkOrderApi;

import com.example.diplomskiandroid.api.WorkOrderPartApi;
import com.example.diplomskiandroid.api.WorkOrderServiceApi;
import com.example.diplomskiandroid.models.Part;
import com.example.diplomskiandroid.models.Service;
import com.example.diplomskiandroid.models.WorkOrder;
import com.example.diplomskiandroid.models.WorkOrderPartCreateRequest;
import com.example.diplomskiandroid.models.WorkOrderPartItem;
import com.example.diplomskiandroid.models.WorkOrderServiceCreateRequest;
import com.example.diplomskiandroid.models.WorkOrderServiceItem;
import com.example.diplomskiandroid.models.WorkOrderUpdateRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkOrderDetailsActivity extends AppCompatActivity {
    private TextView txtOrderNumber, txtStatus, txtVehicle, txtLicensePlate,
            txtYear, txtFuelType, txtMileage, txtVin, txtOwnerName,
            txtOwnerPhone, txtOwnerEmail, txtProblemDescription,
            txtEstimatedCost, txtFinalCost, txtNoServices, txtNoParts, btnBack;
    private TextInputEditText etDiagnosis, etFinalReport;
    private RecyclerView rvServices, rvParts;
    private MaterialButton btnSave, btnAddService, btnAddPart;
    private WorkOrderServiceAdapter serviceAdapter;
    private WorkOrderPartAdapter partAdapter;
    private WorkOrderApi workOrderApi;
    private ServiceApi serviceApi;
    private WorkOrderServiceApi workOrderServiceApi;
    private PartApi partApi;
    private WorkOrderPartApi workOrderPartApi;
    private final List<WorkOrderServiceItem> workOrderServices = new ArrayList<>();
    private final List<WorkOrderPartItem> workOrderParts = new ArrayList<>();
    private List<Service> services = new ArrayList<>();
    private List<Part> parts = new ArrayList<>();
    private int workOrderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_work_order_details);
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
            int navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            view.setPadding(0, 0, 0, Math.max(imeHeight, navBar));
            return insets;
        });
        txtOrderNumber = findViewById(R.id.txtOrderNumber);
        txtStatus = findViewById(R.id.txtStatus);

        txtVehicle = findViewById(R.id.txtVehicle);
        txtLicensePlate = findViewById(R.id.txtLicensePlate);
        txtYear = findViewById(R.id.txtYear);
        txtFuelType = findViewById(R.id.txtFuelType);
        txtMileage = findViewById(R.id.txtMileage);
        txtVin = findViewById(R.id.txtVin);

        txtOwnerName = findViewById(R.id.txtOwnerName);
        txtOwnerPhone = findViewById(R.id.txtOwnerPhone);
        txtOwnerEmail = findViewById(R.id.txtOwnerEmail);

        txtProblemDescription = findViewById(R.id.txtProblemDescription);

        etDiagnosis = findViewById(R.id.etDiagnosis);
        etFinalReport = findViewById(R.id.etFinalReport);

        txtEstimatedCost = findViewById(R.id.txtEstimatedCost);
        txtFinalCost = findViewById(R.id.txtFinalCost);
        btnSave = findViewById(R.id.btnSaveWorkOrder);

        btnAddService = findViewById(R.id.btnAddService);

        workOrderId = getIntent().getIntExtra("workOrderId", 0);

        workOrderApi = ApiClient.getClient(this)
                .create(WorkOrderApi.class);

        serviceApi = ApiClient.getClient(this)
                .create(ServiceApi.class);

        workOrderServiceApi = ApiClient.getClient(this)
                .create(WorkOrderServiceApi.class);

        partApi = ApiClient.getClient(this)
                .create(PartApi.class);

        workOrderPartApi = ApiClient.getClient(this)
                .create(WorkOrderPartApi.class);

        btnBack = findViewById(R.id.txtBack);
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> updateWorkOrder());
        btnAddService.setOnClickListener(v -> loadServices());
        btnAddPart = findViewById(R.id.btnAddPart);
        btnAddPart.setOnClickListener(v -> loadParts());

        rvServices = findViewById(R.id.rvServices);
        txtNoServices = findViewById(R.id.txtNoServices);

        rvServices.setLayoutManager(new LinearLayoutManager(this));
        serviceAdapter = new WorkOrderServiceAdapter(workOrderServices,
                item -> deleteServiceItem(item)
        );
        rvServices.setAdapter(serviceAdapter);


        rvParts = findViewById(R.id.rvParts);
        txtNoParts = findViewById(R.id.txtNoParts);

        rvParts.setLayoutManager(new LinearLayoutManager(this));

        partAdapter = new WorkOrderPartAdapter(
                workOrderParts,
                item -> deletePartItem(item)
        );

        rvParts.setAdapter(partAdapter);

        loadWorkOrderParts();
        loadWorkOrder();
        loadWorkOrderServices();
    }

    private void loadParts() {

        partApi.getAllParts().enqueue(new Callback<List<Part>>() {

            @Override
            public void onResponse(Call<List<Part>> call,
                                   Response<List<Part>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    parts = response.body();

                    showAddPartDialog();

                } else {
                    Toast.makeText(
                            WorkOrderDetailsActivity.this,
                            "Greška kod dohvaćanja autodijelova.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<List<Part>> call,
                                  Throwable t) {

                Toast.makeText(
                        WorkOrderDetailsActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
    private void loadWorkOrderParts() {

        workOrderPartApi.getByWorkOrderId(workOrderId)
                .enqueue(new Callback<List<WorkOrderPartItem>>() {

                    @Override
                    public void onResponse(Call<List<WorkOrderPartItem>> call,
                                           Response<List<WorkOrderPartItem>> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            partAdapter.updateData(response.body());

                            txtNoParts.setVisibility(
                                    response.body().isEmpty()
                                            ? View.VISIBLE
                                            : View.GONE
                            );
                        }
                    }

                    @Override
                    public void onFailure(Call<List<WorkOrderPartItem>> call,
                                          Throwable t) {

                    }
                });
    }

    private void deletePartItem(WorkOrderPartItem item) {

        workOrderPartApi.deletePartItem(item.getId())
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call,
                                           Response<Void> response) {

                        if (response.isSuccessful()) {

                            showMessageDialog(
                                    "Autodio je uspješno obrisan.",
                                    () -> {
                                        loadWorkOrderParts();
                                        loadWorkOrder();
                                    }
                            );
                        } else {

                            Toast.makeText(
                                    WorkOrderDetailsActivity.this,
                                    "Greška kod brisanja autodijela.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call,
                                          Throwable t) {

                        Toast.makeText(
                                WorkOrderDetailsActivity.this,
                                "Greška povezivanja.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void deleteServiceItem(WorkOrderServiceItem item) {
        workOrderServiceApi.deleteServiceItem(item.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            showMessageDialog("Usluga je uspješno obrisana.", () -> {
                                loadWorkOrderServices();
                                loadWorkOrder();
                            });

                        } else {
                            Toast.makeText(
                                    WorkOrderDetailsActivity.this,
                                    "Greška kod brisanja usluge.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(
                                WorkOrderDetailsActivity.this,
                                "Greška povezivanja.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void loadWorkOrderServices() {
        workOrderServiceApi.getByWorkOrderId(workOrderId)
                .enqueue(new Callback<List<WorkOrderServiceItem>>() {
                    @Override
                    public void onResponse(Call<List<WorkOrderServiceItem>> call,
                                           Response<List<WorkOrderServiceItem>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            serviceAdapter.updateData(response.body());
                            txtNoServices.setVisibility(response.body().isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<WorkOrderServiceItem>> call, Throwable t) {
                    }
                });
    }

    private void loadServices() {

        serviceApi.getAllServices().enqueue(new Callback<List<Service>>() {

            @Override
            public void onResponse(Call<List<Service>> call,
                                   Response<List<Service>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    services = response.body();

                    showAddServiceDialog();

                } else {

                    Toast.makeText(
                            WorkOrderDetailsActivity.this,
                            "Greška kod dohvaćanja usluga.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<List<Service>> call,
                                  Throwable t) {

                Toast.makeText(
                        WorkOrderDetailsActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void showAddServiceDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater()
                .inflate(R.layout.dialog_add_work_order_service, null);

        builder.setView(view);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        AutoCompleteTextView actService =
                view.findViewById(R.id.actService);

        TextInputEditText etHours =
                view.findViewById(R.id.etHours);

        TextInputEditText etHourlyRate =
                view.findViewById(R.id.etHourlyRate);

        MaterialButton btnCancel =
                view.findViewById(R.id.btnCancel);

        MaterialButton btnAdd =
                view.findViewById(R.id.btnAdd);

        List<String> serviceNames = new ArrayList<>();

        for (Service service : services) {
            serviceNames.add(service.getName());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        serviceNames);

        actService.setAdapter(adapter);

        final Service[] selectedService = {null};

        actService.setOnItemClickListener((parent, view1, position, id) -> {

            selectedService[0] = services.get(position);

            etHourlyRate.setText(
                    String.valueOf(selectedService[0].getPrice())
            );

        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {

            if (selectedService[0] == null) {

                Toast.makeText(
                        this,
                        "Odaberite uslugu.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (etHours.getText() == null ||
                    etHours.getText().toString().isEmpty()) {

                etHours.setError("Unesite sate");

                return;
            }

            addServiceToWorkOrder(
                    selectedService[0].getId(),
                    Double.parseDouble(etHours.getText().toString()),
                    Double.parseDouble(etHourlyRate.getText().toString())
            );

            dialog.dismiss();

        });

        dialog.show();
    }

    private void addServiceToWorkOrder(int serviceId, double hours, double hourlyRate) {

        WorkOrderServiceCreateRequest request =
                new WorkOrderServiceCreateRequest(
                        workOrderId,
                        serviceId,
                        hours,
                        hourlyRate
                );

        workOrderServiceApi.addServiceToWorkOrder(request)
                .enqueue(new Callback<WorkOrderServiceItem>() {
                    @Override
                    public void onResponse(Call<WorkOrderServiceItem> call,
                                           Response<WorkOrderServiceItem> response) {

                        if (response.isSuccessful()) {
                            Toast.makeText(
                                    WorkOrderDetailsActivity.this,
                                    "Usluga je dodana.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadWorkOrderServices();
                            loadWorkOrder();
                                            }
                        else {
                            Toast.makeText(
                                    WorkOrderDetailsActivity.this,
                                    "Greška kod dodavanja usluge.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WorkOrderServiceItem> call,
                                          Throwable t) {

                        Toast.makeText(
                                WorkOrderDetailsActivity.this,
                                "Greška povezivanja.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void updateWorkOrder() {
        WorkOrderUpdateRequest request =
                new WorkOrderUpdateRequest(
                        etDiagnosis.getText().toString().trim(),
                        etFinalReport.getText().toString().trim()
                );

        workOrderApi.updateWorkOrder(workOrderId, request)
                .enqueue(new Callback<WorkOrder>() {

                    @Override
                    public void onResponse(Call<WorkOrder> call,
                                           Response<WorkOrder> response) {

                        if (response.isSuccessful()) {

                            showMessageDialog("Radni nalog je uspješno spremljen.", () -> {
                                loadWorkOrder();
                                finish();
                            });

                        } else {

                            Toast.makeText(
                                    WorkOrderDetailsActivity.this,
                                    "Greška kod spremanja.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WorkOrder> call,
                                          Throwable t) {

                        Toast.makeText(
                                WorkOrderDetailsActivity.this,
                                "Greška povezivanja.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
    private void showMessageDialog(String message, Runnable onOk) {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_success);
        dialog.setCancelable(false);

        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        MaterialButton btnOk = dialog.findViewById(R.id.btnOk);

        txtMessage.setText(message);

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();

            if (onOk != null) {
                onOk.run();
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }

        dialog.show();
    }

    private void loadWorkOrder() {

        workOrderApi.getWorkOrderDetails(workOrderId)
                .enqueue(new Callback<WorkOrder>() {

                    @Override
                    public void onResponse(Call<WorkOrder> call,
                                           Response<WorkOrder> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            showWorkOrder(response.body());

                        } else {

                            Toast.makeText(
                                    WorkOrderDetailsActivity.this,
                                    "Greška kod dohvaćanja.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WorkOrder> call,
                                          Throwable t) {

                        Toast.makeText(
                                WorkOrderDetailsActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void showWorkOrder(WorkOrder workOrder) {
              var vehicle = workOrder.getServiceRequest().getVehicle();
            var owner = workOrder.getServiceRequest().getUser();

            txtOrderNumber.setText(workOrder.getOrderNumber());
            txtStatus.setText(workOrder.getStatus());
            txtVehicle.setText(vehicle.getBrand() + " " + vehicle.getModel());
            txtLicensePlate.setText(vehicle.getLicensePlate());
            txtYear.setText(String.valueOf(vehicle.getYear()));
            txtFuelType.setText(vehicle.getFuelType());
            txtMileage.setText(vehicle.getMileage() + " km");
            txtVin.setText(vehicle.getVin());

            txtOwnerName.setText(owner.getFirstName() + " " + owner.getLastName());
            txtOwnerPhone.setText(owner.getPhoneNumber());
            txtOwnerEmail.setText(owner.getEmail());

            txtProblemDescription.setText(workOrder.getServiceRequest().getProblemDescription());
            etDiagnosis.setText(workOrder.getDiagnosis());
            etFinalReport.setText(workOrder.getFinalReport());
            txtEstimatedCost.setText(workOrder.getEstimatedCost() + " €");
            txtFinalCost.setText(workOrder.getFinalCost() + " €");

    }

    private void showAddPartDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater()
                .inflate(R.layout.dialog_add_work_order_part, null);

        builder.setView(view);

        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        AutoCompleteTextView actPart = view.findViewById(R.id.actPart);
        TextInputEditText etQuantity = view.findViewById(R.id.etQuantity);
        TextInputEditText etPartPrice = view.findViewById(R.id.etPartPrice);

        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);
        MaterialButton btnAdd = view.findViewById(R.id.btnAdd);

        List<String> partNames = new ArrayList<>();

        for (Part part : parts) {
            partNames.add(part.getName());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        partNames
                );

        actPart.setAdapter(adapter);
        actPart.setThreshold(1);
        final Part[] selectedPart = {null};

        actPart.setOnItemClickListener((parent, view1, position, id) -> {
            selectedPart[0] = parts.get(position);
            etPartPrice.setText(String.valueOf(selectedPart[0].getPrice()));
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {

            if (selectedPart[0] == null) {
                Toast.makeText(this, "Odaberite autodio.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (etQuantity.getText() == null ||
                    etQuantity.getText().toString().trim().isEmpty()) {
                etQuantity.setError("Unesite količinu");
                return;
            }

            int quantity = Integer.parseInt(etQuantity.getText().toString().trim());

            if (quantity <= 0) {
                etQuantity.setError("Količina mora biti veća od 0");
                return;
            }

            addPartToWorkOrder(selectedPart[0].getId(), quantity);

            dialog.dismiss();
        });

        dialog.show();
    }
    private void addPartToWorkOrder(int partId, int quantity) {

        WorkOrderPartCreateRequest request =
                new WorkOrderPartCreateRequest(
                        workOrderId,
                        partId,
                        quantity
                );

        workOrderPartApi.addPartToWorkOrder(request)
                .enqueue(new Callback<WorkOrderPartItem>() {

                    @Override
                    public void onResponse(Call<WorkOrderPartItem> call,
                                           Response<WorkOrderPartItem> response) {

                        if (response.isSuccessful()) {

                            showMessageDialog("Autodio je uspješno dodan.", () -> {
                                loadWorkOrderParts();
                                loadWorkOrder();
                            });

                            loadWorkOrderParts();
                            loadWorkOrder();

                        } else {

                            Toast.makeText(
                                    WorkOrderDetailsActivity.this,
                                    "Greška kod dodavanja autodijela.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<WorkOrderPartItem> call,
                                          Throwable t) {

                        Toast.makeText(
                                WorkOrderDetailsActivity.this,
                                "Greška povezivanja.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });


    }


}