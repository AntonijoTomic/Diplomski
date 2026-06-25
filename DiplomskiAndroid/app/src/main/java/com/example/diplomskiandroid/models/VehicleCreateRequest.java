package com.example.diplomskiandroid.models;

public class VehicleCreateRequest {
    private int userId;
    private String brand;
    private String model;
    private int year;
    private String licensePlate;
    private String vin;
    private String fuelType;
    private int mileage;
    private String registrationDate;
    private String note;

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