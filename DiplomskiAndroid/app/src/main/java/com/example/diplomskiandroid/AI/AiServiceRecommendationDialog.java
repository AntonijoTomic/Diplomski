package com.example.diplomskiandroid.AI;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diplomskiandroid.R;
import com.example.diplomskiandroid.models.AiRecommendedService;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AiServiceRecommendationDialog {

    public interface OnServicesSelectedListener {
        void onServicesSelected(
                List<AiRecommendedService> selectedServices
        );
    }

    private final Context context;

    public AiServiceRecommendationDialog(Context context) {
        this.context = context;
    }

    public void show(
            List<AiRecommendedService> recommendedServices,
            OnServicesSelectedListener listener
    ) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_ai_services, null);

        dialog.setContentView(view);
        dialog.setCancelable(true);

        LinearLayout layoutRecommendations =
                view.findViewById(R.id.layoutRecommendations);

        TextView txtNoRecommendations =
                view.findViewById(R.id.txtNoRecommendations);

        MaterialButton btnCancelAi =
                view.findViewById(R.id.btnCancelAi);

        MaterialButton btnAddAiServices =
                view.findViewById(R.id.btnAddAiServices);

        List<CheckBox> checkBoxes = new ArrayList<>();

        if (recommendedServices == null
                || recommendedServices.isEmpty()) {

            txtNoRecommendations.setVisibility(View.VISIBLE);
            layoutRecommendations.setVisibility(View.GONE);
            btnAddAiServices.setVisibility(View.GONE);

        } else {

            txtNoRecommendations.setVisibility(View.GONE);
            layoutRecommendations.setVisibility(View.VISIBLE);
            btnAddAiServices.setVisibility(View.VISIBLE);

            for (AiRecommendedService service : recommendedServices) {

                CheckBox checkBox = new CheckBox(context);

                checkBox.setText(
                        service.getName()
                                + "\nSati: "
                                + service.getHours()
                );

                checkBox.setTextSize(15);

                checkBox.setTextColor(
                        context.getResources()
                                .getColor(R.color.text_dark)
                );

                checkBox.setChecked(true);
                checkBox.setTag(service);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                params.setMargins(0, 8, 0, 8);
                checkBox.setLayoutParams(params);

                layoutRecommendations.addView(checkBox);
                checkBoxes.add(checkBox);
            }
        }

        btnCancelAi.setOnClickListener(v -> dialog.dismiss());

        btnAddAiServices.setOnClickListener(v -> {

            List<AiRecommendedService> selectedServices =
                    new ArrayList<>();

            for (CheckBox checkBox : checkBoxes) {

                if (checkBox.isChecked()) {
                    selectedServices.add(
                            (AiRecommendedService) checkBox.getTag()
                    );
                }
            }

            if (selectedServices.isEmpty()) {

                Toast.makeText(
                        context,
                        "Odaberite barem jednu uslugu.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            dialog.dismiss();

            if (listener != null) {
                listener.onServicesSelected(selectedServices);
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (
                            context.getResources()
                                    .getDisplayMetrics()
                                    .widthPixels * 0.92
                    ),
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
        }
    }
}