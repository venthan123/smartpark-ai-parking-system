package com.smartpark.repository;

import com.smartpark.model.Slot;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SlotRepository {

    private final JdbcTemplate jdbc;

    public SlotRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Slot> slotMapper = (rs, rowNum) -> {
        Slot s = new Slot();
        s.setId(rs.getInt("id"));
        s.setSlotCode(rs.getString("slot_code"));
        s.setOccupied(rs.getBoolean("is_occupied"));
        s.setPlateNumber(rs.getString("plate_number"));
        return s;
    };

    public List<Slot> findAll() {
        return jdbc.query("SELECT * FROM slots ORDER BY slot_code", slotMapper);
    }

    public Optional<Slot> findFreeSlot() {
        List<Slot> result = jdbc.query(
                "SELECT * FROM slots WHERE is_occupied=FALSE LIMIT 1", slotMapper);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public void occupySlot(String slotCode, String plate) {
        jdbc.update("UPDATE slots SET is_occupied=TRUE, plate_number=? WHERE slot_code=?",
                plate.toUpperCase(), slotCode);
    }

    public void freeSlot(String plate) {
        jdbc.update("UPDATE slots SET is_occupied=FALSE, plate_number=NULL WHERE plate_number=?",
                plate.toUpperCase());
    }

    public int countFree() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM slots WHERE is_occupied=FALSE", Integer.class);
        return count != null ? count : 0;
    }
}
