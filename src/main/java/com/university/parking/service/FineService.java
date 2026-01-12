package com.university.parking.service;

import com.university.parking.domain.Fine;
import com.university.parking.domain.FineStrategy;

import java.util.List;

/**
 * Service interface for fine management operations.
 * Handles fine strategy configuration, calculation, and tracking.
 * Requirements: 5.1-5.6
 */
public interface FineService {
    
    /**
     * Sets the fine calculation strategy.
     * The new strategy will be applied to all future fine calculations.
     * 
     * @param strategy the fine strategy to use
     */
    void setFineStrategy(FineStrategy strategy);
    
    /**
     * Gets the current fine calculation strategy.
     * 
     * @return the current strategy
     */
    FineStrategy getCurrentStrategy();
    
    /**
     * Calculates a fine based on the current strategy.
     * 
     * @param overtimeDuration the overtime duration in hours
     * @return the calculated fine amount
     */
    double calculateFine(int overtimeDuration);
    
    /**
     * Issues a new fine for a vehicle.
     * 
     * @param licensePlate the vehicle's license plate
     * @param amount the fine amount
     * @param reason the reason for the fine
     * @return the created fine
     */
    Fine issueFine(String licensePlate, double amount, String reason);
    
    /**
     * Gets all unpaid fines for a specific license plate.
     * 
     * @param licensePlate the vehicle's license plate
     * @return list of unpaid fines
     */
    List<Fine> getUnpaidFines(String licensePlate);
    
    /**
     * Gets the total amount of unpaid fines for a license plate.
     * 
     * @param licensePlate the vehicle's license plate
     * @return the total unpaid fine amount
     */
    double getTotalUnpaidAmount(String licensePlate);
    
    /**
     * Marks a fine as paid.
     * 
     * @param fineId the fine's unique identifier
     */
    void markFineAsPaid(String fineId);
    
    /**
     * Gets all unpaid fines in the system.
     * 
     * @return list of all unpaid fines
     */
    List<Fine> getAllUnpaidFines();
}
