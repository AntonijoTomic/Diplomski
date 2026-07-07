package com.example.diplomskiandroid.models;

public class WorkOrderUpdateRequest {

    private String diagnosis;
    private String finalReport;

    public WorkOrderUpdateRequest(String diagnosis, String finalReport) {
        this.diagnosis = diagnosis;
        this.finalReport = finalReport;
    }
}