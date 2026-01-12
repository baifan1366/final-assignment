package com.university.parking.domain;

import java.util.Collections;
import java.util.List;

/**
 * Represents an SUV or truck vehicle type.
 * SUVs/Trucks can only park in REGULAR spots due to their size.
 * Requirements: 2.1, 2.3
 */
public class SUVTruck extends Vehicle {

    /**
     * Creates a new SUVTruck with the specified license plate.
     * 
     * @param licensePlate the SUV/truck's license plate
     * @throws IllegalArgumentException if licensePlate is null or empty
     */
    public SUVTruck(String licensePlate) {
        super(licensePlate);
    }

    @Override
    public List<SpotType> getAllowedSpotTypes() {
        return Collections.singletonList(SpotType.REGULAR);
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.SUV_TRUCK;
    }
}
