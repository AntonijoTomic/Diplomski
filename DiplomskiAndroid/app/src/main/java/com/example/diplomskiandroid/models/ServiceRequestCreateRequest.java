package com.example.diplomskiandroid.models;

public class ServiceRequestCreateRequest {

    private int userId;
    private int vehicleId;
    private String problemDescription;
    private String serviceType;
    private String desiredDate;
    private String urgency;
    private String note;
    private int currentMileage;

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