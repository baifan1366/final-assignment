package com.university.parking.dao;

import com.university.parking.db.DatabaseManager;
import com.university.parking.domain.ParkingSpot;
import com.university.parking.domain.SpotStatus;
import com.university.parking.domain.SpotType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ParkingSpotDAO using SQLite database.
 * Requirements: 9.2, 9.3
 */
public class ParkingSpotDAOImpl implements ParkingSpotDAO {
    
    private final DatabaseManager dbManager;
    
    public ParkingSpotDAOImpl(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public ParkingSpot findById(String spotId) {
        String sql = "SELECT * FROM parking_spot WHERE spot_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, spotId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToParkingSpot(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding parking spot by ID: " + spotId, e);
        }
        return null;
    }
    
    @Override
    public List<ParkingSpot> findAll() {
        List<ParkingSpot> spots = new ArrayList<>();
        String sql = "SELECT * FROM parking_spot";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                spots.add(mapResultSetToParkingSpot(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all parking spots", e);
        }
        return spots;
    }

    
    @Override
    public void save(ParkingSpot spot) {
        String sql = "INSERT INTO parking_spot (spot_id, floor_id, type, status, hourly_rate, current_vehicle_plate) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, spot.getSpotId());
            stmt.setString(2, extractFloorId(spot.getSpotId()));
            stmt.setString(3, spot.getType().name());
            stmt.setString(4, spot.getStatus().name());
            stmt.setDouble(5, spot.getHourlyRate());
            stmt.setString(6, spot.getCurrentVehiclePlate());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving parking spot: " + spot.getSpotId(), e);
        }
    }
    
    @Override
    public void update(ParkingSpot spot) {
        String sql = "UPDATE parking_spot SET type = ?, status = ?, hourly_rate = ?, current_vehicle_plate = ? " +
                     "WHERE spot_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, spot.getType().name());
            stmt.setString(2, spot.getStatus().name());
            stmt.setDouble(3, spot.getHourlyRate());
            stmt.setString(4, spot.getCurrentVehiclePlate());
            stmt.setString(5, spot.getSpotId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating parking spot: " + spot.getSpotId(), e);
        }
    }
    
    @Override
    public void delete(String spotId) {
        String sql = "DELETE FROM parking_spot WHERE spot_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, spotId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting parking spot: " + spotId, e);
        }
    }
    
    @Override
    public List<ParkingSpot> findAvailableByType(SpotType type) {
        List<ParkingSpot> spots = new ArrayList<>();
        String sql = "SELECT * FROM parking_spot WHERE type = ? AND status = 'AVAILABLE'";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                spots.add(mapResultSetToParkingSpot(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding available spots by type: " + type, e);
        }
        return spots;
    }
    
    @Override
    public ParkingSpot findByVehiclePlate(String licensePlate) {
        String sql = "SELECT * FROM parking_spot WHERE current_vehicle_plate = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToParkingSpot(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding spot by vehicle plate: " + licensePlate, e);
        }
        return null;
    }
    
    @Override
    public void updateStatus(String spotId, SpotStatus status) {
        String sql = "UPDATE parking_spot SET status = ? WHERE spot_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, spotId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating spot status: " + spotId, e);
        }
    }
    
    @Override
    public List<ParkingSpot> findAllAvailable() {
        List<ParkingSpot> spots = new ArrayList<>();
        String sql = "SELECT * FROM parking_spot WHERE status = 'AVAILABLE'";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                spots.add(mapResultSetToParkingSpot(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all available spots", e);
        }
        return spots;
    }
    
    @Override
    public List<ParkingSpot> findByFloorId(String floorId) {
        List<ParkingSpot> spots = new ArrayList<>();
        String sql = "SELECT * FROM parking_spot WHERE floor_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, floorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                spots.add(mapResultSetToParkingSpot(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding spots by floor: " + floorId, e);
        }
        return spots;
    }
    
    /**
     * Maps a ResultSet row to a ParkingSpot object.
     */
    private ParkingSpot mapResultSetToParkingSpot(ResultSet rs) throws SQLException {
        String spotId = rs.getString("spot_id");
        SpotType type = SpotType.valueOf(rs.getString("type"));
        double hourlyRate = rs.getDouble("hourly_rate");
        SpotStatus status = SpotStatus.valueOf(rs.getString("status"));
        String vehiclePlate = rs.getString("current_vehicle_plate");
        
        ParkingSpot spot = new ParkingSpot(spotId, type, hourlyRate);
        
        // If spot is occupied, assign the vehicle
        if (status == SpotStatus.OCCUPIED && vehiclePlate != null) {
            spot.assignVehicle(vehiclePlate);
        }
        
        return spot;
    }
    
    /**
     * Extracts floor ID from spot ID (assumes format like "F1-S01").
     */
    private String extractFloorId(String spotId) {
        if (spotId != null && spotId.contains("-")) {
            return spotId.substring(0, spotId.indexOf("-"));
        }
        return "F1"; // Default floor
    }
}
