package com.university.parking.dao;

import com.university.parking.domain.Payment;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Access Object interface for Payment entities.
 * Provides standard CRUD operations for payment records.
 * Requirements: 9.2, 9.3
 */
public interface PaymentDAO extends GenericDAO<Payment, String> {
    
    /**
     * Finds all payments for a specific license plate.
     * 
     * @param licensePlate the vehicle's license plate
     * @return list of payments for the license plate
     */
    List<Payment> findByLicensePlate(String licensePlate);
    
    /**
     * Calculates the total revenue within a date range.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the total revenue amount
     */
    double getTotalRevenue(LocalDate startDate, LocalDate endDate);
    
    /**
     * Finds all payments within a date range.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of payments within the date range
     */
    List<Payment> findByDateRange(LocalDate startDate, LocalDate endDate);
}
