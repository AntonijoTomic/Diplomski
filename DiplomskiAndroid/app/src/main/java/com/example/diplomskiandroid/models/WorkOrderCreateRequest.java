package com.example.diplomskiandroid.models;

public class WorkOrderCreateRequest {
    private final int serviceRequestId;
    private final Integer adminId;

    public WorkOrderCreateRequest(int serviceRequestId, Integer adminId) {
        this.serviceRequestId = serviceRequestId;
        this.adminId = adminId;
    }
}