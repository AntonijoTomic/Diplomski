package com.example.diplomskiandroid.api;

import com.example.diplomskiandroid.models.WorkOrderPartCreateRequest;
import com.example.diplomskiandroid.models.WorkOrderPartItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WorkOrderPartApi {

    @GET("api/WorkOrderParts/work-order/{workOrderId}")
    Call<List<WorkOrderPartItem>> getByWorkOrderId(
            @Path("workOrderId") int workOrderId);

    @POST("api/WorkOrderParts")
    Call<WorkOrderPartItem> addPartToWorkOrder(
            @Body WorkOrderPartCreateRequest request);

    @DELETE("api/WorkOrderParts/{id}")
    Call<Void> deletePartItem(@Path("id") int id);
}