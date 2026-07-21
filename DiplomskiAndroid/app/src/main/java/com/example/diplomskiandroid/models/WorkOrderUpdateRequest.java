package com.example.diplomskiandroid.models;

public class WorkOrderUpdateRequest {

    private final String diagnosis;
    private final String finalReport;

    public WorkOrderUpdateRequest(
            String diagnosis,
            String finalReport) {
        this.diagnosis = diagnosis;
        this.finalReport = finalReport;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getFinalReport() {
        return finalReport;
    }
}