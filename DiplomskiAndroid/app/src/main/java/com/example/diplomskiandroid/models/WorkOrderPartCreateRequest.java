package com.example.diplomskiandroid.models;

public class WorkOrderPartCreateRequest {

    private final int workOrderId;
    private final int partId;
    private final int quantity;

    public WorkOrderPartCreateRequest(int workOrderId,
                                      int partId,
                                      int quantity) {

        this.workOrderId = workOrderId;
        this.partId = partId;
        this.quantity = quantity;
    }
}