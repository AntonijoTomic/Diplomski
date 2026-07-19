package com.example.diplomskiandroid.models;

public class AiRecommendedService {

    private int id;
    private String name;
    private double hours;
    private double hourlyRate;
    private boolean selected = true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public double getHours() {
        return hours;
    }
    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }
}