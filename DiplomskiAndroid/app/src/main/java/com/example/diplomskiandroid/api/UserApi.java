package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.UpdateProfileRequest;
import com.example.diplomskiandroid.models.UserProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UserApi {

    @GET("api/Users/profile")
    Call<UserProfile> getProfile();

    @PUT("api/Users/profile")
    Call<Void> updateProfile(
            @Body UpdateProfileRequest request
    );
}