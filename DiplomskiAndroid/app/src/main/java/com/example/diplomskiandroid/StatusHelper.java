package com.example.diplomskiandroid;

import android.widget.TextView;

import com.example.diplomskiandroid.R;

public class StatusHelper {

    public static void applyStatus(TextView textView, String status) {

        if (status == null) return;

        switch (status.toUpperCase()) {

            case "PENDING":
                textView.setText("Na čekanju");
                textView.setBackgroundResource(R.drawable.bg_status_pending);
                break;

            case "APPROVED":
                textView.setText("Odobren");
                textView.setBackgroundResource(R.drawable.bg_status_approved);
                break;

            case "REJECTED":
                textView.setText("Odbijen");
                textView.setBackgroundResource(R.drawable.bg_status_rejected);
                break;

            case "OPEN":
                textView.setText("Otvoren");
                textView.setBackgroundResource(R.drawable.bg_status_open);
                break;

            case "IN_PROGRESS":
                textView.setText("U tijeku");
                textView.setBackgroundResource(R.drawable.bg_status_in_progress);
                break;

            case "COMPLETED":
                textView.setText("Završen");
                textView.setBackgroundResource(R.drawable.bg_status_completed);
                break;

            default:
                textView.setText(status);
                textView.setBackgroundResource(R.drawable.bg_status_pending);
                break;
        }
    }
}