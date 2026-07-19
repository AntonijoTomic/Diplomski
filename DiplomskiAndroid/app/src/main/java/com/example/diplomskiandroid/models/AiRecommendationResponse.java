package com.example.diplomskiandroid.models;

import java.util.List;

public class AiRecommendationResponse {

    private List<AiRecommendedPart> parts;
    private List<AiRecommendedService> services;

    public List<AiRecommendedPart> getParts() {
        return parts;
    }

    public void setParts(List<AiRecommendedPart> parts) {
        this.parts = parts;
    }

    public List<AiRecommendedService> getServices() {
        return services;
    }

    public void setServices(List<AiRecommendedService> services) {
        this.services = services;
    }
}