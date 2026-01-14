package com.university.parking.domain;

import java.util.Collections;
import java.util.List;

/**
 * Represents a bus vehicle type.
 * Demonstrates future-proof design - adding a new vehicle type requires:
 * 1. Create this class extending Vehicle
 * 2. Add BUS to VehicleType enum
 * 3. Add case to VehicleFactory
 * 
 * Buses can only park in RESERVED spots due to their large size.
 */
public class Bus extends Vehicle {

    /**
     * Creates a new Bus with the specified license plate.
     * 
     * @param licensePlate the bus's license plate
     * @throws IllegalArgumentException if licensePlate is null or empty
     */
    public Bus(String licensePlate) {
        super(licensePlate);
    }

    @Override
    public List<SpotType> getAllowedSpotTypes() {
        // Buses can only park in Reserved spots (large vehicle area)
        return Collections.singletonList(SpotType.RESERVED);
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.BUS;
    }
}
