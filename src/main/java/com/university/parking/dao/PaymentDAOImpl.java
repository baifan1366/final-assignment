package com.university.parking.dao;

import com.university.parking.db.DatabaseManager;
import com.university.parking.domain.Payment;
import com.university.parking.domain.PaymentMethod;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of PaymentDAO using SQLite database.
 * Requirements: 9.2, 9.3
 */
public class PaymentDAOImpl implements PaymentDAO {
    
    private final DatabaseManager dbManager;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public PaymentDAOImpl(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    @Override
    public Payment findById(String paymentId) {
        String sql = "SELECT * FROM payment WHERE payment_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paymentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPayment(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payment by ID: " + paymentId, e);
        }
        return null;
    }
    
    @Override
    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all payments", e);
        }
        return payments;
    }

    
    @Override
    public void save(Payment payment) {
        String sql = "INSERT INTO payment (payment_id, amount, method, payment_time, license_plate, ticket_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payment.getPaymentId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getMethod().name());
            stmt.setString(4, payment.getPaymentTime().format(FORMATTER));
            stmt.setString(5, payment.getLicensePlate());
            stmt.setString(6, payment.getTicketId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving payment: " + payment.getPaymentId(), e);
        }
    }
    
    @Override
    public void update(Payment payment) {
        String sql = "UPDATE payment SET amount = ?, method = ?, payment_time = ?, license_plate = ?, ticket_id = ? " +
                     "WHERE payment_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, payment.getAmount());
            stmt.setString(2, payment.getMethod().name());
            stmt.setString(3, payment.getPaymentTime().format(FORMATTER));
            stmt.setString(4, payment.getLicensePlate());
            stmt.setString(5, payment.getTicketId());
            stmt.setString(6, payment.getPaymentId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating payment: " + payment.getPaymentId(), e);
        }
    }
    
    @Override
    public void delete(String paymentId) {
        String sql = "DELETE FROM payment WHERE payment_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paymentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting payment: " + paymentId, e);
        }
    }
    
    @Override
    public List<Payment> findByLicensePlate(String licensePlate) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE license_plate = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payments by license plate: " + licensePlate, e);
        }
        return payments;
    }
    
    @Override
    public double getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM payment " +
                     "WHERE DATE(payment_time) >= ? AND DATE(payment_time) <= ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, startDate.toString());
            stmt.setString(2, endDate.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating total revenue", e);
        }
        return 0.0;
    }
    
    @Override
    public List<Payment> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE DATE(payment_time) >= ? AND DATE(payment_time) <= ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, startDate.toString());
            stmt.setString(2, endDate.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payments by date range", e);
        }
        return payments;
    }
    
    /**
     * Maps a ResultSet row to a Payment object.
     */
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        String paymentId = rs.getString("payment_id");
        double amount = rs.getDouble("amount");
        PaymentMethod method = PaymentMethod.valueOf(rs.getString("method"));
        LocalDateTime paymentTime = LocalDateTime.parse(rs.getString("payment_time"), FORMATTER);
        String licensePlate = rs.getString("license_plate");
        String ticketId = rs.getString("ticket_id");
        
        return new Payment(paymentId, amount, method, paymentTime, licensePlate, ticketId);
    }
}
