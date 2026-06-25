package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.Vehicle;
import com.example.diplomskiandroid.models.VehicleCreateRequest;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import retrofit2.http.Body;
import retrofit2.http.POST;

public interface VehicleApi {

    @GET("api/Vehicles/user/{userId}")
    Call<List<Vehicle>> getVehiclesByUser(@Path("userId") int userId);
    @POST("api/Vehicles")
    Call<Vehicle> createVehicle(@Body VehicleCreateRequest request);
}