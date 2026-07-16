package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.AiRecommendationResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AiApi {

    @POST("api/AiRecommendations/work-orders/{workOrderId}/recommend-parts")
    Call<AiRecommendationResponse> recommendParts(
            @Path("workOrderId") int workOrderId
    );
}