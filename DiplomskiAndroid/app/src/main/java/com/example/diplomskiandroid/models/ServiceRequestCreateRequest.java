package com.example.diplomskiandroid.models;

public class ServiceRequestCreateRequest {

    private final int userId;
    private final int vehicleId;
    private final String problemDescription;
    private final String serviceType;
    private final String desiredDate;
    private final String urgency;
    private final String note;
    private final int currentMileage;

    public ServiceRequestCreateRequest(int userId,
                                       int vehicleId,
                                       String problemDescription,
                                       String serviceType,
                                       String desiredDate,
                                       String urgency,
                                       String note,
                                       int currentMileage) {
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.problemDescription = problemDescription;
        this.serviceType = serviceType;
        this.desiredDate = desiredDate;
        this.urgency = urgency;
        this.note = note;
        this.currentMileage = currentMileage;
    }
}