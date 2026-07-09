package com.example.diplomskiandroid.models;

public class WorkOrderPartCreateRequest {

    private int workOrderId;
    private int partId;
    private int quantity;

    public WorkOrderPartCreateRequest(int workOrderId,
                                      int partId,
                                      int quantity) {

        this.workOrderId = workOrderId;
        this.partId = partId;
        this.quantity = quantity;
    }
}