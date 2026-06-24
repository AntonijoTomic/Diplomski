package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.Vehicle;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface VehicleApi {

    @GET("api/Vehicles/user/{userId}")
    Call<List<Vehicle>> getVehiclesByUser(@Path("userId") int userId);
}