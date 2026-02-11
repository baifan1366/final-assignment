package com.university.parking.dao;

import com.university.parking.db.DatabaseManager;
import com.university.parking.domain.Vehicle;
import com.university.parking.domain.VehicleFactory;
import com.university.parking.domain.VehicleType;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of VehicleDAO using SQLite database.
 * Requirements: 9.2, 9.3
 */
public class VehicleDAOImpl implements VehicleDAO {
    
    private final DatabaseManager dbManager;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public VehicleDAOImpl(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public Vehicle findById(String licensePlate) {
        return findByLicensePlate(licensePlate);
    }
    
    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicle";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all vehicles", e);
        }
        return vehicles;
    }

    
    @Override
    public void save(Vehicle vehicle) {
        String sql = "INSERT INTO vehicle (license_plate, vehicle_type, entry_time, exit_time, spot_id) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getLicensePlate());
            stmt.setString(2, vehicle.getVehicleType().name());
            stmt.setString(3, vehicle.getEntryTime() != null ? vehicle.getEntryTime().format(FORMATTER) : null);
            stmt.setString(4, vehicle.getExitTime() != null ? vehicle.getExitTime().format(FORMATTER) : null);
            stmt.setString(5, null); // spot_id will be updated separately
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving vehicle: " + vehicle.getLicensePlate(), e);
        }
    }
    
    @Override
    public void update(Vehicle vehicle) {
        String sql = "UPDATE vehicle SET vehicle_type = ?, entry_time = ?, exit_time = ? " +
                     "WHERE license_plate = ? AND entry_time = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getVehicleType().name());
            stmt.setString(2, vehicle.getEntryTime() != null ? vehicle.getEntryTime().format(FORMATTER) : null);
            stmt.setString(3, vehicle.getExitTime() != null ? vehicle.getExitTime().format(FORMATTER) : null);
            stmt.setString(4, vehicle.getLicensePlate());
            stmt.setString(5, vehicle.getEntryTime() != null ? vehicle.getEntryTime().format(FORMATTER) : null);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating vehicle: " + vehicle.getLicensePlate(), e);
        }
    }
    
    @Override
    public void delete(String licensePlate) {
        String sql = "DELETE FROM vehicle WHERE license_plate = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting vehicle: " + licensePlate, e);
        }
    }
    
    @Override
    public Vehicle findByLicensePlate(String licensePlate) {
        String sql = "SELECT * FROM vehicle WHERE license_plate = ? ORDER BY entry_time DESC LIMIT 1";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToVehicle(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding vehicle by license plate: " + licensePlate, e);
        }
        return null;
    }
    
    @Override
    public Vehicle findActiveByLicensePlate(String licensePlate) {
        String sql = "SELECT * FROM vehicle WHERE license_plate = ? AND exit_time IS NULL ORDER BY entry_time DESC LIMIT 1";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToVehicle(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active vehicle by license plate: " + licensePlate, e);
        }
        return null;
    }
    
    @Override
    public List<Vehicle> findCurrentlyParked() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicle WHERE entry_time IS NOT NULL AND exit_time IS NULL";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding currently parked vehicles", e);
        }
        return vehicles;
    }
    
    /**
     * Maps a ResultSet row to a Vehicle object.
     */
    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        String licensePlate = rs.getString("license_plate");
        VehicleType vehicleType = VehicleType.valueOf(rs.getString("vehicle_type"));
        String entryTimeStr = rs.getString("entry_time");
        String exitTimeStr = rs.getString("exit_time");
        
        Vehicle vehicle = VehicleFactory.createVehicle(vehicleType, licensePlate);
        
        if (entryTimeStr != null) {
            vehicle.setEntryTime(LocalDateTime.parse(entryTimeStr, FORMATTER));
        }
        if (exitTimeStr != null) {
            vehicle.setExitTime(LocalDateTime.parse(exitTimeStr, FORMATTER));
        }
        
        return vehicle;
    }
}
