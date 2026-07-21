package com.example.diplomskiandroid.models;

public class WorkOrderServiceCreateRequest {
    private final int workOrderId;
    private final int serviceId;
    private final double hours;
    private final double hourlyRate;

    public WorkOrderServiceCreateRequest(int workOrderId, int serviceId, double hours, double hourlyRate) {
        this.workOrderId = workOrderId;
        this.serviceId = serviceId;
        this.hours = hours;
        this.hourlyRate = hourlyRate;
    }
}