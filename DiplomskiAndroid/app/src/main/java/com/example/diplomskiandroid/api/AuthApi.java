package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.LoginRequest;
import com.example.diplomskiandroid.models.LoginResponse;
import com.example.diplomskiandroid.models.RegisterRequest;
import com.example.diplomskiandroid.models.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("api/Auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/Auth/register")
    Call<RegisterResponse> register(
            @Body RegisterRequest request
    );
}
