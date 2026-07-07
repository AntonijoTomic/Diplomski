package com.example.diplomskiandroid.models;

public class WorkOrderServiceCreateRequest {
    private int workOrderId;
    private int serviceId;
    private double hours;
    private double hourlyRate;

    public WorkOrderServiceCreateRequest(int workOrderId, int serviceId, double hours, double hourlyRate) {
        this.workOrderId = workOrderId;
        this.serviceId = serviceId;
        this.hours = hours;
        this.hourlyRate = hourlyRate;
    }
}