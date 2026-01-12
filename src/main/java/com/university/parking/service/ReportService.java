package com.university.parking.service;

import com.university.parking.domain.Fine;
import com.university.parking.domain.ParkingSpot;
import com.university.parking.domain.Vehicle;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for reporting operations.
 * Provides parking lot statistics and reports.
 * Requirements: 7.1-7.6
 */
public interface ReportService {
    
    /**
     * Gets the total revenue within a date range.
     * Aggregates all parking fees and paid fines.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the total revenue amount
     */
    double getTotalRevenue(LocalDate startDate, LocalDate endDate);
    
    /**
     * Gets all vehicles currently parked in the parking lot.
     * 
     * @return list of currently parked vehicles
     */
    List<Vehicle> getCurrentlyParkedVehicles();
    
    /**
     * Gets all outstanding (unpaid) fines.
     * 
     * @return list of unpaid fines
     */
    List<Fine> getOutstandingFines();
    
    /**
     * Gets the current occupancy rate of the parking lot.
     * 
     * @return occupancy rate as a decimal (0.0 to 1.0)
     */
    double getOccupancyRate();
    
    /**
     * Gets the total number of parking spots.
     * 
     * @return total number of spots
     */
    int getTotalSpots();
    
    /**
     * Gets the number of available parking spots.
     * 
     * @return number of available spots
     */
    int getAvailableSpots();
    
    /**
     * Gets all parking spots with their current status.
     * 
     * @return list of all parking spots
     */
    List<ParkingSpot> getAllSpots();
}
