package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.DashboardSummary;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DashboardApi {

    @GET("api/dashboard/summary")
    Call<DashboardSummary> getSummary();
}