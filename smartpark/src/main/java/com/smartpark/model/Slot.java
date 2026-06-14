package com.smartpark.model;

import java.time.LocalDateTime;

public class Slot {
    private int id;
    private String slotCode;
    private boolean isOccupied;
    private String plateNumber;
    private LocalDateTime updatedAt;

    public Slot() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSlotCode() { return slotCode; }
    public void setSlotCode(String slotCode) { this.slotCode = slotCode; }

    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { isOccupied = occupied; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}