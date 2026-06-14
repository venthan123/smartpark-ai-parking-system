package com.smartpark.model;

import java.time.LocalDateTime;

public class Alert {
    private int id;
    private String plateNumber;
    private LocalDateTime alertTime;
    private String message;
    private boolean isResolved;

    public Alert() {}

    public Alert(String plateNumber, String message) {
        this.plateNumber = plateNumber;
        this.alertTime = LocalDateTime.now();
        this.message = message;
        this.isResolved = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public LocalDateTime getAlertTime() { return alertTime; }
    public void setAlertTime(LocalDateTime alertTime) { this.alertTime = alertTime; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isResolved() { return isResolved; }
    public void setResolved(boolean resolved) { isResolved = resolved; }
}