package com.smartpark.model;

import java.time.LocalDateTime;

public class ParkingLog {
    private int id;
    private String plateNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Integer durationMinutes;
    private Double feeAmount;
    private String status; // PARKED or EXITED

    // Constructors
    public ParkingLog() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Double getFeeAmount() { return feeAmount; }
    public void setFeeAmount(Double feeAmount) { this.feeAmount = feeAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}