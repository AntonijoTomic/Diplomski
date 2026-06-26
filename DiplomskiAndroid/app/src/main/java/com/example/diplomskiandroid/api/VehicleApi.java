package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.Vehicle;
import com.example.diplomskiandroid.models.VehicleCreateRequest;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import retrofit2.http.Body;
import retrofit2.http.POST;

public interface VehicleApi {

    @GET("api/Vehicles/user/{userId}")
    Call<List<Vehicle>> getVehiclesByUser(@Path("userId") int userId);
    @POST("api/Vehicles")
    Call<Vehicle> createVehicle(@Body VehicleCreateRequest request);
    @GET("api/Vehicles/{id}")
    Call<Vehicle> getVehicleById(@Path("id") int id);
    @PUT("api/Vehicles/{id}")
    Call<Vehicle> updateVehicle(@Path("id") int id, @Body VehicleCreateRequest request);
    @DELETE("api/Vehicles/{id}")
    Call<Void> deleteVehicle(@Path("id") int id);
}