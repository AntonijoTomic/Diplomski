package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.Service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ServiceApi {

    @GET("api/Services")
    Call<List<Service>> getAllServices();
}