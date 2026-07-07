package com.example.diplomskiandroid.models;

public class WorkOrderServiceItem {
    private int id;
    private int workOrderId;
    private int serviceId;
    private double hours;
    private double hourlyRate;
    private double totalPrice;
    private Service service;



    public int getId() { return id; }
    public int getWorkOrderId() { return workOrderId; }
    public int getServiceId() { return serviceId; }
    public double getHours() { return hours; }
    public double getHourlyRate() { return hourlyRate; }
    public double getTotalPrice() { return totalPrice; }
    public Service getService() {
        return service;
    }
}
