package com.example.diplomskiandroid.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.PartApi;
import com.example.diplomskiandroid.models.Part;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPartActivity extends AppCompatActivity {

    private TextInputLayout tilName;
    private TextInputLayout tilManufacturer;
    private TextInputLayout tilPrice;
    private TextInputLayout tilStockQuantity;
    private TextInputLayout tilMinimumStock;

    private TextInputEditText etName;
    private TextInputEditText etManufacturer;
    private TextInputEditText etPrice;
    private TextInputEditText etStockQuantity;
    private TextInputEditText etMinimumStock;

    private MaterialButton btnSavePart;

    private PartApi partApi;
    private boolean isEditMode = false;
    private int partId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_part);

        initializeViews();

        partApi = ApiClient
                .getClient(this)
                .create(PartApi.class);

        setupEditMode();

        btnSavePart.setOnClickListener(v -> savePart());

    }

    private void setupEditMode() {

        if (!getIntent().hasExtra("partId")) {
            return;
        }

        isEditMode = true;

        partId = getIntent().getIntExtra("partId", 0);

        String name = getIntent().getStringExtra("name");
        String manufacturer =
                getIntent().getStringExtra("manufacturer");

        double price =
                getIntent().getDoubleExtra("price", 0);

        int stockQuantity =
                getIntent().getIntExtra("stockQuantity", 0);

        int minimumStock =
                getIntent().getIntExtra("minimumStock", 0);

        etName.setText(name);
        etManufacturer.setText(manufacturer);
        etPrice.setText(String.valueOf(price));
        etStockQuantity.setText(String.valueOf(stockQuantity));
        etMinimumStock.setText(String.valueOf(minimumStock));

        TextView txtTitle =
                findViewById(R.id.txtAddPartTitle);

        TextView txtSubtitle =
                findViewById(R.id.txtAddPartSubtitle);

        txtTitle.setText("Uredi količinu");
        txtSubtitle.setText("Ažurirajte stanje zalihe autodijela");

        btnSavePart.setText("Spremi promjene");

        etName.setEnabled(false);
        etManufacturer.setEnabled(false);
        etPrice.setEnabled(false);
        etMinimumStock.setEnabled(false);

        etStockQuantity.setEnabled(true);
    }

    private void initializeViews() {
        tilName = findViewById(R.id.tilName);
        tilManufacturer = findViewById(R.id.tilManufacturer);
        tilPrice = findViewById(R.id.tilPrice);
        tilStockQuantity = findViewById(R.id.tilStockQuantity);
        tilMinimumStock = findViewById(R.id.tilMinimumStock);

        etName = findViewById(R.id.etName);
        etManufacturer = findViewById(R.id.etManufacturer);
        etPrice = findViewById(R.id.etPrice);
        etStockQuantity = findViewById(R.id.etStockQuantity);
        etMinimumStock = findViewById(R.id.etMinimumStock);

        btnSavePart = findViewById(R.id.btnSavePart);
    }

    private void savePart() {
           String name = getText(etName);
        String manufacturer = getText(etManufacturer);
        String priceText = getText(etPrice);
        String stockText = getText(etStockQuantity);
        String minimumStockText = getText(etMinimumStock);

        boolean isValid = true;

        if (name.isEmpty()) {
            tilName.setError("Unesite naziv dijela");
            isValid = false;
        }

        if (priceText.isEmpty()) {
            tilPrice.setError("Unesite cijenu");
            isValid = false;
        }

        if (stockText.isEmpty()) {
            tilStockQuantity.setError("Unesite količinu");
            isValid = false;
        }

        if (minimumStockText.isEmpty()) {
            tilMinimumStock.setError("Unesite minimalnu zalihu");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        double price;
        int stockQuantity;
        int minimumStock;

        try {
            price = Double.parseDouble(
                    priceText.replace(",", ".")
            );

            stockQuantity = Integer.parseInt(stockText);
            minimumStock = Integer.parseInt(minimumStockText);

        } catch (NumberFormatException e) {
            Toast.makeText(
                    this,
                    "Provjerite unesene brojčane vrijednosti",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (price < 0) {
            tilPrice.setError("Cijena ne može biti negativna");
            return;
        }

        if (stockQuantity < 0) {
            tilStockQuantity.setError(
                    "Količina ne može biti negativna"
            );
            return;
        }

        if (minimumStock < 0) {
            tilMinimumStock.setError(
                    "Minimalna zaliha ne može biti negativna"
            );
            return;
        }

        Part part = new Part();

        part.setName(name);
        part.setManufacturer(
                manufacturer.isEmpty() ? null : manufacturer
        );
        part.setPrice(price);
        part.setStockQuantity(stockQuantity);
        part.setMinimumStock(minimumStock);

        if (isEditMode) {
            sendUpdateRequest(part);
        } else {
            sendCreateRequest(part);
        }
    }

    private void sendUpdateRequest(Part part) {
        setLoading(true);

        partApi.updatePart(partId, part)
                .enqueue(new Callback<Part>() {
                    @Override
                    public void onResponse(
                            Call<Part> call,
                            Response<Part> response
                    ) {
                        setLoading(false);

                        if (response.isSuccessful()) {
                            Toast.makeText(
                                    AddPartActivity.this,
                                    "Količina je uspješno ažurirana",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();

                        } else {
                            Toast.makeText(
                                    AddPartActivity.this,
                                    "Ažuriranje količine nije uspjelo",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Part> call,
                            Throwable throwable
                    ) {
                        setLoading(false);

                        Toast.makeText(
                                AddPartActivity.this,
                                "Greška: " + throwable.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void sendCreateRequest(Part part) {
        setLoading(true);

        partApi.createPart(part).enqueue(new Callback<Part>() {
            @Override
            public void onResponse(
                    Call<Part> call,
                    Response<Part> response
            ) {
                setLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(
                            AddPartActivity.this,
                            "Autodio je uspješno dodan",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                } else {
                    Toast.makeText(
                            AddPartActivity.this,
                            "Dodavanje autodijela nije uspjelo",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<Part> call,
                    Throwable throwable
            ) {
                setLoading(false);

                Toast.makeText(
                        AddPartActivity.this,
                        "Greška: " + throwable.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }


    private String getText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }

        return editText
                .getText()
                .toString()
                .trim();
    }

    private void setLoading(boolean loading) {
        btnSavePart.setEnabled(!loading);

        btnSavePart.setText(
                loading
                        ? "Spremanje..."
                        : "Spremi autodio"
        );
    }
}