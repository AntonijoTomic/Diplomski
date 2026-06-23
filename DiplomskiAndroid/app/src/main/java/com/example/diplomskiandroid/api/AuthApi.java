package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.LoginRequest;
import com.example.diplomskiandroid.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("api/Auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
