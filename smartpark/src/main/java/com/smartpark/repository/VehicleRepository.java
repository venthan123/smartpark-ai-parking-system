package com.smartpark.repository;

import com.smartpark.model.Vehicle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VehicleRepository {

    private final JdbcTemplate jdbc;

    public VehicleRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Vehicle> vehicleMapper = (rs, rowNum) -> {
        Vehicle v = new Vehicle();
        v.setId(rs.getInt("id"));
        v.setPlateNumber(rs.getString("plate_number"));
        v.setOwnerName(rs.getString("owner_name"));
        v.setVehicleType(rs.getString("vehicle_type"));
        v.setAuthorized(rs.getBoolean("is_authorized"));
        return v;
    };

    public Optional<Vehicle> findByPlate(String plate) {
        String sql = "SELECT * FROM vehicles WHERE plate_number = ?";
        List<Vehicle> result = jdbc.query(sql, vehicleMapper, plate.toUpperCase());
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public boolean isAuthorized(String plate) {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE plate_number = ? AND is_authorized = TRUE";
        Integer count = jdbc.queryForObject(sql, Integer.class, plate.toUpperCase());
        return count != null && count > 0;
    }

    public List<Vehicle> findAll() {
        return jdbc.query("SELECT * FROM vehicles ORDER BY created_at DESC", vehicleMapper);
    }
}