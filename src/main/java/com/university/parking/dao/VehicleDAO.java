package com.university.parking.dao;

import com.university.parking.domain.Vehicle;

import java.util.List;

/**
 * Data Access Object interface for Vehicle entities.
 * Extends GenericDAO with additional vehicle specific operations.
 * Requirements: 9.2, 9.3
 */
public interface VehicleDAO extends GenericDAO<Vehicle, String> {
    
    /**
     * Finds a vehicle by its license plate.
     * 
     * @param licensePlate the vehicle's license plate
     * @return the vehicle if found, null otherwise
     */
    Vehicle findByLicensePlate(String licensePlate);
    
    Vehicle findActiveByLicensePlate(String licensePlate);
    
    /**
     * Finds all vehicles currently parked in the parking lot.
     * A vehicle is considered currently parked if it has an entry time but no exit time.
     * 
     * @return list of currently parked vehicles
     */
    List<Vehicle> findCurrentlyParked();
}
