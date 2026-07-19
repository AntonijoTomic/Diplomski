package com.example.diplomskiandroid.AI;

import android.content.Context;
import android.widget.Toast;

import com.example.diplomskiandroid.api.AiApi;
import com.example.diplomskiandroid.api.ApiClient;
import com.example.diplomskiandroid.api.WorkOrderPartApi;
import com.example.diplomskiandroid.api.WorkOrderServiceApi;
import com.example.diplomskiandroid.models.AiRecommendationResponse;
import com.example.diplomskiandroid.models.AiRecommendedPart;
import com.example.diplomskiandroid.models.AiRecommendedService;
import com.example.diplomskiandroid.models.WorkOrderPartCreateRequest;
import com.example.diplomskiandroid.models.WorkOrderPartItem;
import com.example.diplomskiandroid.models.WorkOrderServiceCreateRequest;
import com.example.diplomskiandroid.models.WorkOrderServiceItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiManager {

    private final Context context;
    private final AiApi aiApi;
    private final WorkOrderPartApi workOrderPartApi;
    private final WorkOrderServiceApi workOrderServiceApi;
    public AiManager(Context context) {
        this.context = context;

        aiApi = ApiClient.getClient(context)
                .create(AiApi.class);

        workOrderServiceApi = ApiClient.getClient(context)
                .create(WorkOrderServiceApi.class);

        workOrderPartApi = ApiClient.getClient(context)
                .create(WorkOrderPartApi.class);
    }

    public void openPartRecommendations(
            int workOrderId,
            Runnable onFinished
    ) {
        aiApi.recommend(workOrderId)
                .enqueue(new Callback<AiRecommendationResponse>() {

                    @Override
                    public void onResponse(
                            Call<AiRecommendationResponse> call,
                            Response<AiRecommendationResponse> response
                    ) {
                        if (!response.isSuccessful()
                                || response.body() == null) {

                            Toast.makeText(
                                    context,
                                    "Greška kod dohvaćanja AI preporuke.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        new AiPartRecommendationDialog(context).show(
                                response.body().getParts(),
                                selectedParts ->
                                        addSelectedParts(
                                                workOrderId,
                                                selectedParts,
                                                onFinished
                                        )
                        );
                    }

                    @Override
                    public void onFailure(
                            Call<AiRecommendationResponse> call,
                            Throwable t
                    ) {
                        Toast.makeText(
                                context,
                                "Greška povezivanja: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    public void openServiceRecommendations(
            int workOrderId,
            Runnable onFinished
    ) {
        aiApi.recommend(workOrderId)
                .enqueue(new Callback<AiRecommendationResponse>() {

                    @Override
                    public void onResponse(
                            Call<AiRecommendationResponse> call,
                            Response<AiRecommendationResponse> response
                    ) {

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            Toast.makeText(
                                    context,
                                    "Greška kod dohvaćanja AI preporuke.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        new AiServiceRecommendationDialog(context).show(
                                response.body().getServices(),
                                selectedServices ->
                                        addSelectedServices(
                                                workOrderId,
                                                selectedServices,
                                                onFinished
                                        )
                        );
                    }

                    @Override
                    public void onFailure(
                            Call<AiRecommendationResponse> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                context,
                                "Greška povezivanja: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
    private void addSelectedParts(
            int workOrderId,
            List<AiRecommendedPart> selectedParts,
            Runnable onFinished
    ) {
        if (selectedParts == null || selectedParts.isEmpty()) {
            return;
        }

        int total = selectedParts.size();
        int[] completed = {0};
        int[] failed = {0};

        for (AiRecommendedPart part : selectedParts) {

            WorkOrderPartCreateRequest request =
                    new WorkOrderPartCreateRequest(
                            workOrderId,
                            part.getId(),
                            part.getQuantity()
                    );

            workOrderPartApi.addPartToWorkOrder(request)
                    .enqueue(new Callback<WorkOrderPartItem>() {

                        @Override
                        public void onResponse(
                                Call<WorkOrderPartItem> call,
                                Response<WorkOrderPartItem> response
                        ) {
                            if (!response.isSuccessful()) {
                                failed[0]++;
                            }

                            completed[0]++;

                            if (completed[0] == total) {
                                finish(failed[0], onFinished);
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<WorkOrderPartItem> call,
                                Throwable t
                        ) {
                            failed[0]++;
                            completed[0]++;

                            if (completed[0] == total) {
                                finish(failed[0], onFinished);
                            }
                        }
                    });
        }
    }
    private void addSelectedServices(
            int workOrderId,
            List<AiRecommendedService> selectedServices,
            Runnable onFinished
    ) {

        if (selectedServices == null || selectedServices.isEmpty()) {
            return;
        }

        int total = selectedServices.size();
        int[] completed = {0};
        int[] failed = {0};

        for (AiRecommendedService service : selectedServices) {

            WorkOrderServiceCreateRequest request =
                    new WorkOrderServiceCreateRequest(
                            workOrderId,
                            service.getId(),
                            service.getHours(),
                            service.getHourlyRate()
                    );

            workOrderServiceApi.addServiceToWorkOrder(request)
                    .enqueue(new Callback<WorkOrderServiceItem>() {

                        @Override
                        public void onResponse(
                                Call<WorkOrderServiceItem> call,
                                Response<WorkOrderServiceItem> response
                        ) {

                            if (!response.isSuccessful()) {
                                failed[0]++;
                            }

                            completed[0]++;

                            if (completed[0] == total) {
                                finish(failed[0], onFinished);
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<WorkOrderServiceItem> call,
                                Throwable t
                        ) {

                            failed[0]++;
                            completed[0]++;

                            if (completed[0] == total) {
                                finish(failed[0], onFinished);
                            }
                        }
                    });
        }
    }

    private void finish(
            int failed,
            Runnable onFinished
    ) {
        if (onFinished != null) {
            onFinished.run();
        }

        Toast.makeText(
                context,
                failed == 0
                        ? "Predloženi dijelovi uspješno su dodani."
                        : "Neki dijelovi nisu mogli biti dodani.",
                Toast.LENGTH_LONG
        ).show();
    }
}