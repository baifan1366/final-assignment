package com.university.parking.dao;

import com.university.parking.domain.ParkingSpot;
import com.university.parking.domain.SpotStatus;
import com.university.parking.domain.SpotType;

import java.util.List;

/**
 * Data Access Object interface for ParkingSpot entities.
 * Extends GenericDAO with additional parking spot specific operations.
 * Requirements: 9.2, 9.3
 */
public interface ParkingSpotDAO extends GenericDAO<ParkingSpot, String> {
    
    /**
     * Finds all available parking spots of a specific type.
     * 
     * @param type the spot type to filter by
     * @return list of available spots of the specified type
     */
    List<ParkingSpot> findAvailableByType(SpotType type);
    
    /**
     * Finds the parking spot currently occupied by a vehicle with the given license plate.
     * 
     * @param licensePlate the vehicle's license plate
     * @return the parking spot if found, null otherwise
     */
    ParkingSpot findByVehiclePlate(String licensePlate);
    
    /**
     * Updates the status of a parking spot.
     * 
     * @param spotId the spot's unique identifier
     * @param status the new status
     */
    void updateStatus(String spotId, SpotStatus status);
    
    /**
     * Finds all available parking spots regardless of type.
     * 
     * @return list of all available spots
     */
    List<ParkingSpot> findAllAvailable();
    
    /**
     * Finds all parking spots on a specific floor.
     * 
     * @param floorId the floor identifier
     * @return list of spots on the specified floor
     */
    List<ParkingSpot> findByFloorId(String floorId);
}
