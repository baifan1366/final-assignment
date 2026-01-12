package com.university.parking.dao;

import com.university.parking.db.DatabaseManager;
import com.university.parking.domain.Fine;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of FineDAO using SQLite database.
 * Requirements: 9.2, 9.3
 */
public class FineDAOImpl implements FineDAO {
    
    private final DatabaseManager dbManager;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public FineDAOImpl(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public Fine findById(String fineId) {
        String sql = "SELECT * FROM fine WHERE fine_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fineId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToFine(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding fine by ID: " + fineId, e);
        }
        return null;
    }
    
    @Override
    public List<Fine> findAll() {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM fine";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fines.add(mapResultSetToFine(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all fines", e);
        }
        return fines;
    }

    
    @Override
    public void save(Fine fine) {
        String sql = "INSERT INTO fine (fine_id, license_plate, amount, reason, issued_time, paid) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fine.getFineId());
            stmt.setString(2, fine.getLicensePlate());
            stmt.setDouble(3, fine.getAmount());
            stmt.setString(4, fine.getReason());
            stmt.setString(5, fine.getIssuedTime().format(FORMATTER));
            stmt.setInt(6, fine.isPaid() ? 1 : 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving fine: " + fine.getFineId(), e);
        }
    }
    
    @Override
    public void update(Fine fine) {
        String sql = "UPDATE fine SET license_plate = ?, amount = ?, reason = ?, issued_time = ?, paid = ? " +
                     "WHERE fine_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fine.getLicensePlate());
            stmt.setDouble(2, fine.getAmount());
            stmt.setString(3, fine.getReason());
            stmt.setString(4, fine.getIssuedTime().format(FORMATTER));
            stmt.setInt(5, fine.isPaid() ? 1 : 0);
            stmt.setString(6, fine.getFineId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating fine: " + fine.getFineId(), e);
        }
    }
    
    @Override
    public void delete(String fineId) {
        String sql = "DELETE FROM fine WHERE fine_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fineId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting fine: " + fineId, e);
        }
    }
    
    @Override
    public List<Fine> findUnpaidByLicensePlate(String licensePlate) {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM fine WHERE license_plate = ? AND paid = 0";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                fines.add(mapResultSetToFine(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding unpaid fines for: " + licensePlate, e);
        }
        return fines;
    }
    
    @Override
    public double sumUnpaidByLicensePlate(String licensePlate) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM fine WHERE license_plate = ? AND paid = 0";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error summing unpaid fines for: " + licensePlate, e);
        }
        return 0.0;
    }
    
    @Override
    public List<Fine> findAllUnpaid() {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM fine WHERE paid = 0";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fines.add(mapResultSetToFine(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all unpaid fines", e);
        }
        return fines;
    }
    
    @Override
    public void markAsPaid(String fineId) {
        String sql = "UPDATE fine SET paid = 1 WHERE fine_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fineId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error marking fine as paid: " + fineId, e);
        }
    }
    
    /**
     * Maps a ResultSet row to a Fine object.
     */
    private Fine mapResultSetToFine(ResultSet rs) throws SQLException {
        String fineId = rs.getString("fine_id");
        String licensePlate = rs.getString("license_plate");
        double amount = rs.getDouble("amount");
        String reason = rs.getString("reason");
        LocalDateTime issuedTime = LocalDateTime.parse(rs.getString("issued_time"), FORMATTER);
        boolean paid = rs.getInt("paid") == 1;
        
        return new Fine(fineId, licensePlate, amount, reason, issuedTime, paid);
    }
}
