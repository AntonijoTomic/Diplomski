package com.example.diplomskiandroid.models;

import com.example.diplomskiandroid.models.Vehicle;

import java.util.List;

public class UserDashboardResponse {

    private int vehicleCount;
    private int activeServiceRequestCount;
    private double totalServiceCost;
    private List<Vehicle> vehicles;
    private List<MonthlyServiceCost> monthlyServiceCosts;
    public int getVehicleCount() {
        return vehicleCount;
    }

    public int getActiveServiceRequestCount() {
        return activeServiceRequestCount;
    }

    public double getTotalServiceCost() {
        return totalServiceCost;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }
    public List<MonthlyServiceCost> getMonthlyServiceCosts() {
        return monthlyServiceCosts;
    }
}