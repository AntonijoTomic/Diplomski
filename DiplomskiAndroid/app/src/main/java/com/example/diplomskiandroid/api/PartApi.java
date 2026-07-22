package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.Part;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PartApi {

    @GET("api/Parts")
    Call<List<Part>> getAllParts();

    @POST("api/Parts")
    Call<Part> createPart(@Body Part part);
    @PUT("api/Parts/{id}")
    Call<Part> updatePart(
            @Path("id") int id,
            @Body Part part
    );
}