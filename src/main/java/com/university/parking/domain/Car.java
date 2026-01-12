package com.university.parking.domain;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a car vehicle type.
 * Cars can park in COMPACT and REGULAR spots.
 * Requirements: 2.1, 2.3
 */
public class Car extends Vehicle {

    /**
     * Creates a new Car with the specified license plate.
     * 
     * @param licensePlate the car's license plate
     * @throws IllegalArgumentException if licensePlate is null or empty
     */
    public Car(String licensePlate) {
        super(licensePlate);
    }

    @Override
    public List<SpotType> getAllowedSpotTypes() {
        return Arrays.asList(SpotType.COMPACT, SpotType.REGULAR);
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.CAR;
    }
}
