package com.example.diplomskiandroid.models;

public class VehicleCreateRequest {
    private final int userId;
    private final String brand;
    private final String model;
    private final int year;
    private final String licensePlate;
    private final String vin;
    private final String fuelType;
    private final int mileage;
    private final String registrationDate;
    private final String note;

    public VehicleCreateRequest(int userId, String brand, String model, int year,
                                String licensePlate, String vin, String fuelType,
                                int mileage, String registrationDate, String note) {
        this.userId = userId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.vin = vin;
        this.fuelType = fuelType;
        this.mileage = mileage;
        this.registrationDate = registrationDate;
        this.note = note;
    }
}