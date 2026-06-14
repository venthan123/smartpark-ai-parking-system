package com.smartpark.controller;

import com.smartpark.service.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vehicle")
@CrossOrigin(origins = "*")   // allows HTML dashboard and Python to call this API
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    // Python calls this when a plate is detected at ENTRY gate
    // POST /api/vehicle/entry
    // Body: { "plate": "TN09AB1234" }
    @PostMapping("/entry")
    public ResponseEntity<Map<String, Object>> vehicleEntry(@RequestBody Map<String, String> body) {
        String plate = body.get("plate");
        if (plate == null || plate.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Plate number required"));
        }
        Map<String, Object> result = parkingService.processEntry(plate);
        return ResponseEntity.ok(result);
    }

    // Python calls this when a plate is detected at EXIT gate
    // POST /api/vehicle/exit
    // Body: { "plate": "TN09AB1234" }
    @PostMapping("/exit")
    public ResponseEntity<Map<String, Object>> vehicleExit(@RequestBody Map<String, String> body) {
        String plate = body.get("plate");
        if (plate == null || plate.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Plate number required"));
        }
        Map<String, Object> result = parkingService.processExit(plate);
        return ResponseEntity.ok(result);
    }

    // Manual entry override (from dashboard UI)
    // POST /api/vehicle/manual-entry
    @PostMapping("/manual-entry")
    public ResponseEntity<Map<String, Object>> manualEntry(@RequestBody Map<String, String> body) {
        return vehicleEntry(body);
    }

    // Manual exit override (from dashboard UI)
    @PostMapping("/manual-exit")
    public ResponseEntity<Map<String, Object>> manualExit(@RequestBody Map<String, String> body) {
        return vehicleExit(body);
    }
}