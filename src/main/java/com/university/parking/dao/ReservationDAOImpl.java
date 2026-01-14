package com.university.parking.dao;

import com.university.parking.db.DatabaseManager;
import com.university.parking.domain.Reservation;
import com.university.parking.domain.ReservationStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLite implementation of ReservationDAO.
 * Demonstrates future-proof design with complete DAO implementation.
 */
public class ReservationDAOImpl implements ReservationDAO {
    
    private final DatabaseManager dbManager;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public ReservationDAOImpl(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        createTableIfNotExists();
    }
    
    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS reservation (" +
                "reservation_id TEXT PRIMARY KEY, " +
                "license_plate TEXT NOT NULL, " +
                "spot_id TEXT NOT NULL, " +
                "reservation_time TEXT NOT NULL, " +
                "start_time TEXT NOT NULL, " +
                "end_time TEXT NOT NULL, " +
                "status TEXT NOT NULL)";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create reservation table", e);
        }
    }
    
    @Override
    public void save(Reservation reservation) {
        String sql = "INSERT INTO reservation (reservation_id, license_plate, spot_id, " +
                "reservation_time, start_time, end_time, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservation.getReservationId());
            stmt.setString(2, reservation.getLicensePlate());
            stmt.setString(3, reservation.getSpotId());
            stmt.setString(4, reservation.getReservationTime().format(FORMATTER));
            stmt.setString(5, reservation.getStartTime().format(FORMATTER));
            stmt.setString(6, reservation.getEndTime().format(FORMATTER));
            stmt.setString(7, reservation.getStatus().name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save reservation", e);
        }
    }
    
    @Override
    public Reservation findById(String reservationId) {
        String sql = "SELECT * FROM reservation WHERE reservation_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find reservation", e);
        }
    }
    
    @Override
    public List<Reservation> findAll() {
        String sql = "SELECT * FROM reservation ORDER BY start_time";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reservations.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all reservations", e);
        }
        return reservations;
    }
    
    @Override
    public void update(Reservation reservation) {
        String sql = "UPDATE reservation SET license_plate = ?, spot_id = ?, " +
                "start_time = ?, end_time = ?, status = ? WHERE reservation_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservation.getLicensePlate());
            stmt.setString(2, reservation.getSpotId());
            stmt.setString(3, reservation.getStartTime().format(FORMATTER));
            stmt.setString(4, reservation.getEndTime().format(FORMATTER));
            stmt.setString(5, reservation.getStatus().name());
            stmt.setString(6, reservation.getReservationId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update reservation", e);
        }
    }
    
    @Override
    public void delete(String reservationId) {
        String sql = "DELETE FROM reservation WHERE reservation_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete reservation", e);
        }
    }
    
    @Override
    public List<Reservation> findByLicensePlate(String licensePlate) {
        String sql = "SELECT * FROM reservation WHERE license_plate = ? ORDER BY start_time";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find reservations by license plate", e);
        }
        return reservations;
    }
    
    @Override
    public List<Reservation> findBySpotAndTimeRange(String spotId, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT * FROM reservation WHERE spot_id = ? " +
                "AND start_time < ? AND end_time > ? " +
                "AND status IN ('PENDING', 'CONFIRMED')";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, spotId);
            stmt.setString(2, endTime.format(FORMATTER));
            stmt.setString(3, startTime.format(FORMATTER));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find reservations by spot and time", e);
        }
        return reservations;
    }
    
    @Override
    public List<Reservation> findAllActive() {
        String now = LocalDateTime.now().format(FORMATTER);
        String sql = "SELECT * FROM reservation WHERE status = 'CONFIRMED' " +
                "AND start_time <= ? AND end_time >= ?";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, now);
            stmt.setString(2, now);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find active reservations", e);
        }
        return reservations;
    }
    
    @Override
    public List<Reservation> findExpiredPending() {
        String now = LocalDateTime.now().format(FORMATTER);
        String sql = "SELECT * FROM reservation WHERE status IN ('PENDING', 'CONFIRMED') " +
                "AND end_time < ?";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, now);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find expired reservations", e);
        }
        return reservations;
    }
    
    @Override
    public void updateStatus(String reservationId, String status) {
        String sql = "UPDATE reservation SET status = ? WHERE reservation_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, reservationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update reservation status", e);
        }
    }
    
    private Reservation mapResultSet(ResultSet rs) throws SQLException {
        String licensePlate = rs.getString("license_plate");
        String spotId = rs.getString("spot_id");
        LocalDateTime startTime = LocalDateTime.parse(rs.getString("start_time"), FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(rs.getString("end_time"), FORMATTER);
        
        Reservation reservation = new Reservation(licensePlate, spotId, startTime, endTime);
        
        // Set status using reflection or by recreating with correct status
        String statusStr = rs.getString("status");
        ReservationStatus status = ReservationStatus.valueOf(statusStr);
        if (status == ReservationStatus.CONFIRMED) {
            reservation.confirm();
        } else if (status == ReservationStatus.CANCELLED) {
            reservation.cancel();
        }
        
        return reservation;
    }
}
