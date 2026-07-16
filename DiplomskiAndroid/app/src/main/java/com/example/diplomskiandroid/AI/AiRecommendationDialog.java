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
import com.example.diplomskiandroid.models.AiRecommendedPart;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AiRecommendationDialog {

    public interface OnPartsSelectedListener {
        void onPartsSelected(List<AiRecommendedPart> selectedParts);
    }

    private final Context context;

    public AiRecommendationDialog(Context context) {
        this.context = context;
    }

    public void show(
            List<AiRecommendedPart> recommendedParts,
            OnPartsSelectedListener listener
    ) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_ai_recommendations, null);

        dialog.setContentView(view);
        dialog.setCancelable(true);

        LinearLayout layoutRecommendations =
                view.findViewById(R.id.layoutRecommendations);

        TextView txtNoRecommendations =
                view.findViewById(R.id.txtNoRecommendations);

        MaterialButton btnCancelAi =
                view.findViewById(R.id.btnCancelAi);

        MaterialButton btnAddAiParts =
                view.findViewById(R.id.btnAddAiParts);

        List<CheckBox> checkBoxes = new ArrayList<>();

        if (recommendedParts == null || recommendedParts.isEmpty()) {
            txtNoRecommendations.setVisibility(View.VISIBLE);
            layoutRecommendations.setVisibility(View.GONE);
            btnAddAiParts.setVisibility(View.GONE);
        } else {
            txtNoRecommendations.setVisibility(View.GONE);
            layoutRecommendations.setVisibility(View.VISIBLE);
            btnAddAiParts.setVisibility(View.VISIBLE);

            for (AiRecommendedPart part : recommendedParts) {
                CheckBox checkBox = new CheckBox(context);

                checkBox.setText(part.getName());
                checkBox.setTextSize(15);
                checkBox.setTextColor(
                        context.getResources().getColor(R.color.text_dark)
                );
                checkBox.setChecked(true);
                checkBox.setTag(part);

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

        btnAddAiParts.setOnClickListener(v -> {

            List<AiRecommendedPart> selectedParts = new ArrayList<>();

            for (CheckBox checkBox : checkBoxes) {
                if (checkBox.isChecked()) {
                    selectedParts.add(
                            (AiRecommendedPart) checkBox.getTag()
                    );
                }
            }

            if (selectedParts.isEmpty()) {
                Toast.makeText(
                        context,
                        "Odaberite barem jedan dio.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            dialog.dismiss();

            if (listener != null) {
                listener.onPartsSelected(selectedParts);
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
                    (int) (context.getResources()
                            .getDisplayMetrics().widthPixels * 0.92),
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
        }
    }
}