package com.smartpark.controller;

import com.smartpark.model.*;
import com.smartpark.service.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final ParkingService parkingService;

    public DashboardController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    // GET /api/dashboard/live — all currently parked vehicles
    @GetMapping("/live")
    public ResponseEntity<List<ParkingLog>> getLive() {
        return ResponseEntity.ok(parkingService.getAllParked());
    }

    // GET /api/dashboard/logs — recent 50 entry/exit events
    @GetMapping("/logs")
    public ResponseEntity<List<ParkingLog>> getLogs() {
        return ResponseEntity.ok(parkingService.getRecentLogs());
    }

    // GET /api/dashboard/slots — all slot statuses
    @GetMapping("/slots")
    public ResponseEntity<List<Slot>> getSlots() {
        return ResponseEntity.ok(parkingService.getAllSlots());
    }

    // GET /api/dashboard/alerts — unauthorized vehicle alerts
    @GetMapping("/alerts")
    public ResponseEntity<List<Alert>> getAlerts() {
        return ResponseEntity.ok(parkingService.getAlerts());
    }

    // GET /api/dashboard/summary — counts for dashboard header
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("parkedCount", parkingService.getAllParked().size());
        summary.put("freeSlots", parkingService.getFreeSlots());
        summary.put("alertCount", parkingService.getAlerts().size());
        return ResponseEntity.ok(summary);
    }

    // GET /api/dashboard/bill/{id} — get log details for bill display on UI
    @GetMapping("/bill/{id}")
    public ResponseEntity<?> getBill(@PathVariable int id) {
        return parkingService.getLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}