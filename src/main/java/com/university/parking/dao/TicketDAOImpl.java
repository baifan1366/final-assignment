package com.university.parking.dao;

import com.university.parking.db.DatabaseManager;
import com.university.parking.domain.Ticket;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of TicketDAO using SQLite database.
 * Requirements: 9.2, 9.3
 */
public class TicketDAOImpl implements TicketDAO {
    
    private final DatabaseManager dbManager;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public TicketDAOImpl(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public Ticket findById(String ticketId) {
        String sql = "SELECT * FROM ticket WHERE ticket_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ticketId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTicket(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding ticket by ID: " + ticketId, e);
        }
        return null;
    }
    
    @Override
    public List<Ticket> findAll() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM ticket";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all tickets", e);
        }
        return tickets;
    }

    
    @Override
    public void save(Ticket ticket) {
        String sql = "INSERT INTO ticket (ticket_id, license_plate, spot_id, entry_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ticket.getTicketId());
            stmt.setString(2, ticket.getLicensePlate());
            stmt.setString(3, ticket.getSpotId());
            stmt.setString(4, ticket.getEntryTime().format(FORMATTER));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving ticket: " + ticket.getTicketId(), e);
        }
    }
    
    @Override
    public void update(Ticket ticket) {
        String sql = "UPDATE ticket SET license_plate = ?, spot_id = ?, entry_time = ? WHERE ticket_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ticket.getLicensePlate());
            stmt.setString(2, ticket.getSpotId());
            stmt.setString(3, ticket.getEntryTime().format(FORMATTER));
            stmt.setString(4, ticket.getTicketId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating ticket: " + ticket.getTicketId(), e);
        }
    }
    
    @Override
    public void delete(String ticketId) {
        String sql = "DELETE FROM ticket WHERE ticket_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ticketId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting ticket: " + ticketId, e);
        }
    }
    
    @Override
    public Ticket findByLicensePlate(String licensePlate) {
        String sql = "SELECT * FROM ticket WHERE license_plate = ? ORDER BY entry_time DESC LIMIT 1";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTicket(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding ticket by license plate: " + licensePlate, e);
        }
        return null;
    }
    
    @Override
    public List<Ticket> findActiveTickets() {
        List<Ticket> tickets = new ArrayList<>();
        // Active tickets are those where the vehicle is still parked (has entry but no exit in vehicle table)
        String sql = "SELECT t.* FROM ticket t " +
                     "INNER JOIN vehicle v ON t.license_plate = v.license_plate " +
                     "WHERE v.entry_time IS NOT NULL AND v.exit_time IS NULL";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active tickets", e);
        }
        return tickets;
    }
    
    /**
     * Maps a ResultSet row to a Ticket object.
     */
    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        String ticketId = rs.getString("ticket_id");
        String licensePlate = rs.getString("license_plate");
        String spotId = rs.getString("spot_id");
        LocalDateTime entryTime = LocalDateTime.parse(rs.getString("entry_time"), FORMATTER);
        
        return new Ticket(ticketId, licensePlate, spotId, entryTime);
    }
}
