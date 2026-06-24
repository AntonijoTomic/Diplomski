package com.example.diplomskiandroid.models;

public class Vehicle {
    private int id;
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
    private String createdAt;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public String getLicensePlate() { return licensePlate; }
    public String getVin() { return vin; }
    public String getFuelType() { return fuelType; }
    public int getMileage() { return mileage; }
    public String getRegistrationDate() { return registrationDate; }
    public String getNote() { return note; }
    public String getCreatedAt() { return createdAt; }
}