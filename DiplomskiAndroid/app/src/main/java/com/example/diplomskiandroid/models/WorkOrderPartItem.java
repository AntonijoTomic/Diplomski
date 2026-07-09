package com.example.diplomskiandroid.models;

public class WorkOrderPartItem {

    private int id;
    private int workOrderId;
    private int partId;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private Part part;

    public int getId() {
        return id;
    }

    public int getWorkOrderId() {
        return workOrderId;
    }

    public int getPartId() {
        return partId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Part getPart() {
        return part;
    }
}