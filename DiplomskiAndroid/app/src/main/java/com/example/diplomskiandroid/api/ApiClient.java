package com.example.diplomskiandroid.api;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://192.168.1.74:5136/";

    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {

        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        SharedPreferences preferences = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
                        String token = preferences.getString("token", "");

                        Request originalRequest = chain.request();

                        Request.Builder requestBuilder = originalRequest.newBuilder();

                        if (!token.isEmpty()) {
                            requestBuilder.addHeader("Authorization", "Bearer " + token);
                        }

                        Request newRequest = requestBuilder.build();

                        return chain.proceed(newRequest);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}