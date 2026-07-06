package com.example.diplomskiandroid.models;

public class ServiceRequest {
    private int id;
    private int vehicleId;
    private String vehicleName;
    private String licensePlate;
    private String problemDescription;
    private String serviceType;
    private String urgency;
    private String status;
    private String createdAt;
    private String note;
    private String desiredDate;

    public int getId() { return id; }
    public int getVehicleId() { return vehicleId; }
    public String getVehicleName() { return vehicleName; }
    public String getLicensePlate() { return licensePlate; }
    public String getProblemDescription() { return problemDescription; }
    public String getServiceType() { return serviceType; }
    public String getUrgency() { return urgency; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getNote() {return  note; }

    public String getDesiredDate() {  return desiredDate;  }
}