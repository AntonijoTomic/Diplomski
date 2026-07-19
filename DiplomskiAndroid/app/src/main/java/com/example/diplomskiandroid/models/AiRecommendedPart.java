package com.example.diplomskiandroid.models;

public class AiRecommendedPart {

    private int id;
    private String name;
    private int quantity;
    private boolean selected = true;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}