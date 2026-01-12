package com.university.parking.domain;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a vehicle with handicapped permit.
 * Handicapped vehicles can park in ANY spot type.
 * Special pricing rules apply:
 * - Zero rate in HANDICAPPED spots
 * - RM2/hour in other spot types
 * Requirements: 2.1, 2.3, 2.4, 2.5, 2.6
 */
public class HandicappedVehicle extends Vehicle {

    /** Special hourly rate for handicapped vehicles in non-handicapped spots */
    public static final double SPECIAL_HOURLY_RATE = 2.0;

    /**
     * Creates a new HandicappedVehicle with the specified license plate.
     * 
     * @param licensePlate the vehicle's license plate
     * @throws IllegalArgumentException if licensePlate is null or empty
     */
    public HandicappedVehicle(String licensePlate) {
        super(licensePlate);
    }

    @Override
    public List<SpotType> getAllowedSpotTypes() {
        return Arrays.asList(SpotType.COMPACT, SpotType.REGULAR, SpotType.HANDICAPPED, SpotType.RESERVED);
    }

    @Override
    public VehicleType getVehicleType() {
        return VehicleType.HANDICAPPED;
    }

    /**
     * Checks if this vehicle is parked in a handicapped spot.
     * 
     * @param spot the parking spot to check
     * @return true if the spot is a HANDICAPPED type
     */
    public boolean isParkedInHandicappedSpot(ParkingSpot spot) {
        return spot != null && spot.getType() == SpotType.HANDICAPPED;
    }

    /**
     * Gets the effective hourly rate for this vehicle based on the spot type.
     * Zero rate for HANDICAPPED spots, RM2/hour for others.
     * 
     * @param spot the parking spot
     * @return the effective hourly rate
     */
    public double getEffectiveHourlyRate(ParkingSpot spot) {
        if (isParkedInHandicappedSpot(spot)) {
            return 0.0;
        }
        return SPECIAL_HOURLY_RATE;
    }
}
