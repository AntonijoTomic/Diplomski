package com.example.diplomskiandroid.models;

public class WorkOrder {
    private int id;
    private String orderNumber;
    private int serviceRequestId;
    private Integer adminId;
    private String diagnosis;
    private String status;
    private double estimatedCost;
    private double finalCost;
    private String openedAt;
    private String closedAt;
    private String finalReport;
    private ServiceRequest serviceRequest;

    public int getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public int getServiceRequestId() { return serviceRequestId; }
    public Integer getAdminId() { return adminId; }
    public String getDiagnosis() { return diagnosis; }
    public String getStatus() { return status; }
    public double getEstimatedCost() { return estimatedCost; }
    public double getFinalCost() { return finalCost; }
    public String getOpenedAt() { return openedAt; }
    public String getClosedAt() { return closedAt; }
    public String getFinalReport() { return finalReport; }

    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }
}