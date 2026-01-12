package com.university.parking.dao;

import com.university.parking.domain.Fine;

import java.util.List;

/**
 * Data Access Object interface for Fine entities.
 * Extends GenericDAO with additional fine specific operations.
 * Requirements: 9.2, 9.3
 */
public interface FineDAO extends GenericDAO<Fine, String> {
    
    /**
     * Finds all unpaid fines for a specific license plate.
     * 
     * @param licensePlate the vehicle's license plate
     * @return list of unpaid fines for the license plate
     */
    List<Fine> findUnpaidByLicensePlate(String licensePlate);
    
    /**
     * Calculates the sum of all unpaid fines for a specific license plate.
     * 
     * @param licensePlate the vehicle's license plate
     * @return the total amount of unpaid fines
     */
    double sumUnpaidByLicensePlate(String licensePlate);
    
    /**
     * Finds all unpaid fines in the system.
     * 
     * @return list of all unpaid fines
     */
    List<Fine> findAllUnpaid();
    
    /**
     * Marks a fine as paid.
     * 
     * @param fineId the fine's unique identifier
     */
    void markAsPaid(String fineId);
}
