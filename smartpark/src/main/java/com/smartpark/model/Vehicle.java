package com.smartpark.model;


import java.time.LocalDateTime;

public class Vehicle {
    private int id;
    private String plateNumber;
    private String ownerName;
    private String vehicleType;
    private boolean isAuthorized;
    private LocalDateTime createdAt;

    // Constructors
    public Vehicle() {}

    public Vehicle(String plateNumber, String ownerName) {
        this.plateNumber = plateNumber;
        this.ownerName = ownerName;
        this.isAuthorized = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public boolean isAuthorized() { return isAuthorized; }
    public void setAuthorized(boolean authorized) { isAuthorized = authorized; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}