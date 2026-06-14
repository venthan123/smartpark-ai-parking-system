package com.smartpark.controller;

import com.smartpark.model.ParkingLog;
import com.smartpark.service.BillService;
import com.smartpark.service.ParkingService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/bill")
@CrossOrigin(origins = "*")
public class BillController {

    private final BillService billService;
    private final ParkingService parkingService;

    public BillController(BillService billService, ParkingService parkingService) {
        this.billService = billService;
        this.parkingService = parkingService;
    }

    // GET /api/bill/pdf/{id} — triggered when user clicks "Generate PDF Bill"
    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> downloadBillPdf(@PathVariable int id) {
        Optional<ParkingLog> logOpt = parkingService.getLogById(id);
        if (logOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] pdf = billService.generateBillPdf(logOpt.get());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "bill_" + logOpt.get().getPlateNumber() + ".pdf");
            return ResponseEntity.ok().headers(headers).body(pdf);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}