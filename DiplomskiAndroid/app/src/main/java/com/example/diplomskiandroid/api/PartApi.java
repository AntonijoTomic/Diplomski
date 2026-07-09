package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.Part;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PartApi {

    @GET("api/Parts")
    Call<List<Part>> getAllParts();
}