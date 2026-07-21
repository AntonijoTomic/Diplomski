package com.example.diplomskiandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.AuthApi;
import com.example.diplomskiandroid.models.RegisterRequest;
import com.example.diplomskiandroid.models.RegisterResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFirstName;
    private TextInputEditText etLastName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private TextInputEditText etPhone;
    private TextInputEditText etAddress;

    private MaterialButton btnRegister;

    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

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

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnRegister = findViewById(R.id.btnRegister);

        authApi = ApiClient.getClient(this).create(AuthApi.class);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String firstName = getText(etFirstName);
        String lastName = getText(etLastName);
        String email = getText(etEmail);
        String password = getText(etPassword);
        String confirmPassword = getText(etConfirmPassword);
        String phone = getText(etPhone);
        String address = getText(etAddress);

        if (firstName.isEmpty()
                || lastName.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

            Toast.makeText(
                    this,
                    "Popunite sva obavezna polja.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Unesite ispravnu email adresu.");
            etEmail.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Lozinka mora imati najmanje 6 znakova.");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Lozinke se ne podudaraju.");
            etConfirmPassword.requestFocus();
            return;
        }

        RegisterRequest request = new RegisterRequest(
                firstName,
                lastName,
                email,
                password,
                phone.isEmpty() ? null : phone,
                address.isEmpty() ? null : address
        );

        btnRegister.setEnabled(false);

        authApi.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(
                    Call<RegisterResponse> call,
                    Response<RegisterResponse> response
            ) {
                btnRegister.setEnabled(true);

                if (response.isSuccessful()) {
                    String message = "Registracija uspješna.";

                    if (response.body() != null
                            && response.body().getMessage() != null
                            && !response.body().getMessage().isEmpty()) {

                        message = response.body().getMessage();
                    }

                    Toast.makeText(
                            RegisterActivity.this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(
                            RegisterActivity.this,
                            LoginActivity.class
                    );

                    intent.putExtra("registeredEmail", email);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(
                            RegisterActivity.this,
                            "Registracija nije uspjela. Email možda već postoji.",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<RegisterResponse> call,
                    Throwable t
            ) {
                btnRegister.setEnabled(true);

                Toast.makeText(
                        RegisterActivity.this,
                        "Greška povezivanja: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private String getText(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }

        return editText.getText().toString().trim();
    }
}