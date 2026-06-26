package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.ServiceRequest;
import com.example.diplomskiandroid.models.ServiceRequestCreateRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServiceRequestApi {

    @GET("api/ServiceRequests/user/{userId}")
    Call<List<ServiceRequest>> getRequestsByUser(@Path("userId") int userId);

    @POST("api/ServiceRequests")
    Call<ServiceRequest> createServiceRequest(
            @Body ServiceRequestCreateRequest request
    );
}