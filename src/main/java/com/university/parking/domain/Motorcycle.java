package com.university.parking.domain;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a motorcycle vehicle type.
 * Motorcycles can park in COMPACT and REGULAR spots.
 * Requirements: 2.1, 2.3
 */
public class Motorcycle extends Vehicle {

    /**
     * Creates a new Motorcycle with the specified license plate.
     * 
     * @param licensePlate the motorcycle's license plate
     * @throws IllegalArgumentException if licensePlate is null or empty
     */
    public Motorcycle(String licensePlate) {
        super(licensePlate);
    }

    @Override
    public List<SpotType> getAllowedSpotTypes() {
        // PDF requirement: Motorcycle can park in Compact spots only
        return Arrays.asList(SpotType.COMPACT);
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.MOTORCYCLE;
    }
}
