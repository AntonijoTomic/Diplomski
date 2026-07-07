package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.WorkOrderServiceCreateRequest;
import com.example.diplomskiandroid.models.WorkOrderServiceItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WorkOrderServiceApi {

    @GET("api/WorkOrderServices/work-order/{workOrderId}")
    Call<List<WorkOrderServiceItem>> getByWorkOrderId(@Path("workOrderId") int workOrderId);

    @POST("api/WorkOrderServices")
    Call<WorkOrderServiceItem> addServiceToWorkOrder(
            @Body WorkOrderServiceCreateRequest request
    );

    @DELETE("api/WorkOrderServices/{id}")
    Call<Void> deleteServiceItem(@Path("id") int id);
}