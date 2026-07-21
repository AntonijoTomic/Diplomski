package com.example.diplomskiandroid.models;

import java.util.List;

public class DashboardSummary {

    private int vehicleCount;
    private int serviceRequestCount;
    private int workOrderCount;
    private int userCount;
    private int pendingServiceRequestCount;
    private int approvedServiceRequestCount;

    private double totalRevenue;
    private double currentMonthRevenue;

    private int lowStockPartCount;

    private List<MonthlyServiceCost> monthlyRevenue;
    private List<Part> lowStockParts;




    public int getVehicleCount() {
        return vehicleCount;
    }

    public int getServiceRequestCount() {
        return serviceRequestCount;
    }

    public int getWorkOrderCount() {
        return workOrderCount;
    }

    public int getUserCount() {
        return userCount;
    }
    public int getPendingServiceRequestCount() {
        return pendingServiceRequestCount;
    }

    public int getApprovedServiceRequestCount() {
        return approvedServiceRequestCount;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public double getCurrentMonthRevenue() {
        return currentMonthRevenue;
    }

    public int getLowStockPartCount() {
        return lowStockPartCount;
    }

    public List<MonthlyServiceCost> getMonthlyRevenue() {
        return monthlyRevenue;
    }
    public List<Part> getLowStockParts() {
        return lowStockParts;
    }
}