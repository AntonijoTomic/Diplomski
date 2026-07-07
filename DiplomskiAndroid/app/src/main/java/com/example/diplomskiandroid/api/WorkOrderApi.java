package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.WorkOrder;
import com.example.diplomskiandroid.models.WorkOrderCreateRequest;
import com.example.diplomskiandroid.models.WorkOrderUpdateRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface WorkOrderApi {

    @POST("api/WorkOrders")
    Call<WorkOrder> createWorkOrder(
            @Body WorkOrderCreateRequest request
    );

    @GET("api/WorkOrders/{id}/details")
    Call<WorkOrder> getWorkOrderDetails(@Path("id") int id);

    @PUT("api/WorkOrders/{id}")
    Call<WorkOrder> updateWorkOrder(
            @Path("id") int id,
            @Body WorkOrderUpdateRequest request
    );
}