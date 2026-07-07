package com.example.diplomskiandroid.models;

public class WorkOrderCreateRequest {
    private int serviceRequestId;
    private Integer adminId;

    public WorkOrderCreateRequest(int serviceRequestId, Integer adminId) {
        this.serviceRequestId = serviceRequestId;
        this.adminId = adminId;
    }
}