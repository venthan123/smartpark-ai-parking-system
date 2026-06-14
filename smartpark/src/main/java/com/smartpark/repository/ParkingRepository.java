package com.smartpark.repository;

import com.smartpark.model.ParkingLog;
import com.smartpark.model.Alert;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ParkingRepository {

    private final JdbcTemplate jdbc;

    public ParkingRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<ParkingLog> logMapper = (rs, rowNum) -> {
        ParkingLog log = new ParkingLog();
        log.setId(rs.getInt("id"));
        log.setPlateNumber(rs.getString("plate_number"));
        Timestamp entry = rs.getTimestamp("entry_time");
        Timestamp exit = rs.getTimestamp("exit_time");
        log.setEntryTime(entry != null ? entry.toLocalDateTime() : null);
        log.setExitTime(exit != null ? exit.toLocalDateTime() : null);
        log.setDurationMinutes(rs.getObject("duration_minutes", Integer.class));
        log.setFeeAmount(rs.getObject("fee_amount", Double.class));
        log.setStatus(rs.getString("status"));
        return log;
    };

    // Record a new vehicle entry
    public void recordEntry(String plate) {
        String sql = "INSERT INTO parking_log (plate_number, entry_time, status) VALUES (?, ?, 'PARKED')";
        jdbc.update(sql, plate.toUpperCase(), LocalDateTime.now());
    }

    // Record vehicle exit and update fee
    public void recordExit(String plate, int durationMinutes, double fee) {
        String sql = "UPDATE parking_log SET exit_time=?, duration_minutes=?, fee_amount=?, status='EXITED' " +
                "WHERE plate_number=? AND status='PARKED'";
        jdbc.update(sql, LocalDateTime.now(), durationMinutes, fee, plate.toUpperCase());
    }

    // Get active (currently parked) vehicle
    public Optional<ParkingLog> findActiveByPlate(String plate) {
        String sql = "SELECT * FROM parking_log WHERE plate_number=? AND status='PARKED' ORDER BY entry_time DESC LIMIT 1";
        List<ParkingLog> result = jdbc.query(sql, logMapper, plate.toUpperCase());
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    // Get log entry by ID (for bill generation)
    public Optional<ParkingLog> findById(int id) {
        String sql = "SELECT * FROM parking_log WHERE id=?";
        List<ParkingLog> result = jdbc.query(sql, logMapper, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    // Get all currently parked vehicles
    public List<ParkingLog> findAllParked() {
        return jdbc.query("SELECT * FROM parking_log WHERE status='PARKED' ORDER BY entry_time DESC", logMapper);
    }

    // Get recent 50 logs for dashboard
    public List<ParkingLog> findRecentLogs() {
        return jdbc.query("SELECT * FROM parking_log ORDER BY entry_time DESC LIMIT 50", logMapper);
    }

    // Save unauthorized vehicle alert
    public void saveAlert(Alert alert) {
        String sql = "INSERT INTO alerts (plate_number, alert_time, message) VALUES (?,?,?)";
        jdbc.update(sql, alert.getPlateNumber(), alert.getAlertTime(), alert.getMessage());
    }

    // Get unresolved alerts
    public List<Alert> getUnresolvedAlerts() {
        RowMapper<Alert> alertMapper = (rs, rowNum) -> {
            Alert a = new Alert();
            a.setId(rs.getInt("id"));
            a.setPlateNumber(rs.getString("plate_number"));
            a.setAlertTime(rs.getTimestamp("alert_time").toLocalDateTime());
            a.setMessage(rs.getString("message"));
            a.setResolved(rs.getBoolean("is_resolved"));
            return a;
        };
        return jdbc.query("SELECT * FROM alerts WHERE is_resolved=FALSE ORDER BY alert_time DESC", alertMapper);
    }
}