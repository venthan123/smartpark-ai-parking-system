package com.smartpark.service;

import com.smartpark.model.*;
import com.smartpark.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ParkingService {

    // Rs. 10 per hour — change this one line to change rate everywhere
    private static final double RATE_PER_HOUR = 10.0;

    private final ParkingRepository parkingRepo;
    private final VehicleRepository vehicleRepo;
    private final SlotRepository slotRepo;

    public ParkingService(ParkingRepository parkingRepo,
                          VehicleRepository vehicleRepo,
                          SlotRepository slotRepo) {
        this.parkingRepo = parkingRepo;
        this.vehicleRepo = vehicleRepo;
        this.slotRepo = slotRepo;
    }

    // Called when Python sends a detected plate for ENTRY
    public Map<String, Object> processEntry(String plate) {
        Map<String, Object> response = new HashMap<>();
        plate = plate.toUpperCase().trim();

        // Check if already parked
        if (parkingRepo.findActiveByPlate(plate).isPresent()) {
            response.put("status", "ALREADY_PARKED");
            response.put("message", plate + " is already inside.");
            return response;
        }

        // Check authorization
        boolean authorized = vehicleRepo.isAuthorized(plate);
        if (!authorized) {
            Alert alert = new Alert(plate, "Unauthorized vehicle attempted entry: " + plate);
            parkingRepo.saveAlert(alert);
            response.put("status", "UNAUTHORIZED");
            response.put("message", "ALERT: Unauthorized vehicle " + plate);
            // Still record entry for logging purposes
        }

        // Assign a slot
        Optional<Slot> freeSlot = slotRepo.findFreeSlot();
        if (freeSlot.isEmpty()) {
            response.put("status", "FULL");
            response.put("message", "Parking lot is full.");
            return response;
        }

        // Record entry and occupy slot
        parkingRepo.recordEntry(plate);
        slotRepo.occupySlot(freeSlot.get().getSlotCode(), plate);

        response.put("status", authorized ? "ENTRY_SUCCESS" : "ENTRY_UNAUTHORIZED");
        response.put("plate", plate);
        response.put("slot", freeSlot.get().getSlotCode());
        response.put("entryTime", LocalDateTime.now().toString());
        response.put("authorized", authorized);
        return response;
    }

    // Called when Python sends a detected plate for EXIT
    public Map<String, Object> processExit(String plate) {
        Map<String, Object> response = new HashMap<>();
        plate = plate.toUpperCase().trim();

        Optional<ParkingLog> active = parkingRepo.findActiveByPlate(plate);
        if (active.isEmpty()) {
            response.put("status", "NOT_FOUND");
            response.put("message", plate + " is not currently parked.");
            return response;
        }

        ParkingLog log = active.get();
        LocalDateTime entryTime = log.getEntryTime();
        LocalDateTime exitTime = LocalDateTime.now();

        // Calculate duration and fee
        long durationMinutes = ChronoUnit.MINUTES.between(entryTime, exitTime);
        double fee = calculateFee(durationMinutes);

        // Update database
        parkingRepo.recordExit(plate, (int) durationMinutes, fee);
        slotRepo.freeSlot(plate);

        response.put("status", "EXIT_SUCCESS");
        response.put("plate", plate);
        response.put("entryTime", entryTime.toString());
        response.put("exitTime", exitTime.toString());
        response.put("durationMinutes", durationMinutes);
        response.put("feeAmount", fee);
        response.put("logId", log.getId());  // used by UI to load bill
        return response;
    }

    // Fee = duration × (rate ÷ 60), rounded to 1 decimal
    public double calculateFee(long durationMinutes) {
        double ratePerMinute = RATE_PER_HOUR / 60.0;
        double fee = durationMinutes * ratePerMinute;
        return Math.round(fee * 10.0) / 10.0;
    }

    // Dashboard data
    public List<ParkingLog> getAllParked()    { return parkingRepo.findAllParked(); }
    public List<ParkingLog> getRecentLogs()  { return parkingRepo.findRecentLogs(); }
    public List<Slot> getAllSlots()           { return slotRepo.findAll(); }
    public List<Alert> getAlerts()           { return parkingRepo.getUnresolvedAlerts(); }
    public int getFreeSlots()                { return slotRepo.countFree(); }

    public Optional<ParkingLog> getLogById(int id) {
        return parkingRepo.findById(id);
    }
}