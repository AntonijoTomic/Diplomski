package com.example.diplomskiandroid.models;

public class Part {

    private int id;
    private String name;
    private String manufacturer;
    private double price;
    private int stockQuantity;
    private int minimumStock;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public int getMinimumStock() {
        return minimumStock;
    }
}