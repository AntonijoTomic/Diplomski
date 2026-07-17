package com.example.diplomskiandroid.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.activities.LoginActivity;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.DashboardApi;
import com.example.diplomskiandroid.api.UserApi;
import com.example.diplomskiandroid.models.DashboardSummary;
import com.example.diplomskiandroid.models.UpdateProfileRequest;
import com.example.diplomskiandroid.models.UserProfile;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView txtProfileName, txtProfileEmail, txtProfileRole,txtCardTitle1 ,txtCardValue1,txtCardTitle2,txtCardValue2 ;
    private DashboardApi dashboardApi;
    private MaterialButton btnLogout;
    private CardView cardFirst,cardSecond, cardEditProfile,cardChangePassword, cardDeactivateAccount;
    private UserApi userApi;
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(
                R.layout.fragment_profile,
                container,
                false
        );
        ViewPager2 viewPager =
                requireActivity().findViewById(R.id.viewPager);

        txtProfileName = view.findViewById(R.id.txtProfileName);
        txtProfileEmail = view.findViewById(R.id.txtProfileEmail);
        txtProfileRole = view.findViewById(R.id.txtProfileRole);
        btnLogout = view.findViewById(R.id.btnLogout);
        txtCardTitle1 = view.findViewById(R.id.txtCardTitle1);
        txtCardValue1 = view.findViewById(R.id.txtCardValue1);

        txtCardTitle2 = view.findViewById(R.id.txtCardTitle2);
        txtCardValue2 = view.findViewById(R.id.txtCardValue2);

        cardFirst = view.findViewById(R.id.cardFirst);
        cardSecond = view.findViewById(R.id.cardSecond);
        cardEditProfile = view.findViewById(R.id.cardEditProfile);
        cardChangePassword = view.findViewById(R.id.cardChangePassword);
        cardDeactivateAccount = view.findViewById(R.id.cardDeactivateAccount);

        userApi = ApiClient
                .getClient(requireContext())
                .create(UserApi.class);

        SharedPreferences preferences =
                requireContext().getSharedPreferences(
                        "USER_SESSION",
                        requireContext().MODE_PRIVATE
                );

        txtProfileName.setText(
                preferences.getString("fullName", "Korisnik")
        );

        txtProfileEmail.setText(
                preferences.getString("email", "-")
        );


        String role = preferences.getString("role", "-");

        if ("ADMIN".equalsIgnoreCase(role)) {

            txtProfileRole.setText("Administrator");

            txtCardTitle1.setText("Servisni zahtjevi");
            txtCardTitle2.setText("Radni nalozi");

            cardFirst.setOnClickListener(v ->
                    viewPager.setCurrentItem(1, true)
            );

            cardSecond.setOnClickListener(v ->
                    viewPager.setCurrentItem(2, true)
            );

        } else {

            txtProfileRole.setText("Korisnik");

            txtCardTitle1.setText("Moja vozila");
            txtCardTitle2.setText("Servisni zahtjevi");

            cardFirst.setOnClickListener(v ->
                    viewPager.setCurrentItem(1, true)
            );

            cardSecond.setOnClickListener(v ->
                    viewPager.setCurrentItem(2, true)
            );
        }

        cardEditProfile.setOnClickListener(v -> showEditProfileDialog());

        cardChangePassword.setOnClickListener(v -> {

        });

        cardDeactivateAccount.setOnClickListener(v -> {

        });

        btnLogout.setOnClickListener(v -> logout());


        dashboardApi = ApiClient.getClient(requireContext())
                .create(DashboardApi.class);

        loadDashboardSummary(role);
        return view;
    }

    private void showEditProfileDialog() {

        userApi.getProfile().enqueue(new Callback<UserProfile>() {

            @Override
            public void onResponse(Call<UserProfile> call,
                                   Response<UserProfile> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(
                            requireContext(),
                            "Greška kod dohvaćanja profila.",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                UserProfile user = response.body();

                Dialog dialog = new Dialog(requireContext());
                dialog.setContentView(R.layout.dialog_edit_profile);

                Objects.requireNonNull(dialog.getWindow())
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextInputEditText etFirstName =
                        dialog.findViewById(R.id.etFirstName);

                TextInputEditText etLastName =
                        dialog.findViewById(R.id.etLastName);

                TextInputEditText etPhone =
                        dialog.findViewById(R.id.etPhone);

                TextInputEditText etAddress =
                        dialog.findViewById(R.id.etAddress);

                MaterialButton btnSave =
                        dialog.findViewById(R.id.btnSaveProfile);

                etFirstName.setText(user.getFirstName());
                etLastName.setText(user.getLastName());
                etPhone.setText(user.getPhoneNumber());
                etAddress.setText(user.getAddress());

                btnSave.setOnClickListener(v -> {


                        String firstName = etFirstName.getText() == null
                                ? ""
                                : etFirstName.getText().toString().trim();

                        String lastName = etLastName.getText() == null
                                ? ""
                                : etLastName.getText().toString().trim();

                        String phone = etPhone.getText() == null
                                ? ""
                                : etPhone.getText().toString().trim();

                        String address = etAddress.getText() == null
                                ? ""
                                : etAddress.getText().toString().trim();

                        if (firstName.isEmpty()) {
                            etFirstName.setError("Unesite ime.");
                            return;
                        }

                        if (lastName.isEmpty()) {
                            etLastName.setError("Unesite prezime.");
                            return;
                        }

                        UpdateProfileRequest request =
                                new UpdateProfileRequest(
                                        firstName,
                                        lastName,
                                        phone,
                                        address
                                );

                        userApi.updateProfile(request)
                                .enqueue(new Callback<Void>() {

                                    @Override
                                    public void onResponse(
                                            Call<Void> call,
                                            Response<Void> response
                                    ) {
                                        if (!response.isSuccessful()) {
                                            Toast.makeText(
                                                    requireContext(),
                                                    "Greška kod spremanja profila.",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                            return;
                                        }

                                        String fullName = firstName + " " + lastName;

                                        requireContext()
                                                .getSharedPreferences(
                                                        "USER_SESSION",
                                                        requireContext().MODE_PRIVATE
                                                )
                                                .edit()
                                                .putString("fullName", fullName)
                                                .apply();

                                        txtProfileName.setText(fullName);

                                        dialog.dismiss();

                                        Toast.makeText(
                                                requireContext(),
                                                "Profil je uspješno ažuriran.",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                    @Override
                                    public void onFailure(
                                            Call<Void> call,
                                            Throwable t
                                    ) {
                                        Toast.makeText(
                                                requireContext(),
                                                "Greška povezivanja.",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                });
                    });


                dialog.show();
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {

                Toast.makeText(
                        requireContext(),
                        "Greška kod povezivanja.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void loadDashboardSummary(String role) {

        dashboardApi.getSummary()
                .enqueue(new Callback<DashboardSummary>() {

                    @Override
                    public void onResponse(
                            Call<DashboardSummary> call,
                            Response<DashboardSummary> response
                    ) {
                        if (!response.isSuccessful()
                                || response.body() == null) {
                            return;
                        }

                        DashboardSummary summary = response.body();

                        if ("ADMIN".equalsIgnoreCase(role)) {
                            txtCardValue1.setText(
                                    summary.getVehicleCount() + " vozila"
                            );

                            txtCardValue2.setText(
                                    summary.getWorkOrderCount()
                                            + " radnih naloga"
                            );
                        } else {
                            txtCardValue1.setText(
                                    summary.getVehicleCount()
                                            + " registriranih vozila"
                            );

                            txtCardValue2.setText(
                                    summary.getServiceRequestCount()
                                            + " servisnih zahtjeva"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<DashboardSummary> call,
                            Throwable t
                    ) {
                    }
                });
    }

    private void logout() {
        requireContext()
                .getSharedPreferences(
                        "USER_SESSION",
                        requireContext().MODE_PRIVATE
                )
                .edit()
                .clear()
                .apply();

        Intent intent = new Intent(
                requireContext(),
                LoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        requireActivity().finish();
    }
}