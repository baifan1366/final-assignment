package com.university.parking.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Abstract base class for all vehicle types in the parking system.
 * Uses inheritance to define vehicle-specific spot type compatibility.
 * Requirements: 2.2, 2.3
 */
public abstract class Vehicle {
    private final String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    /**
     * Creates a new Vehicle with the specified license plate.
     * 
     * @param licensePlate the vehicle's license plate (non-null, non-empty)
     * @throws IllegalArgumentException if licensePlate is null or empty
     */
    protected Vehicle(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be null or empty");
        }
        this.licensePlate = licensePlate;
        this.entryTime = null;
        this.exitTime = null;
    }

    /**
     * Returns the list of spot types this vehicle is allowed to park in.
     * Each subclass defines its own compatibility rules.
     * 
     * @return list of compatible SpotType values
     */
    public abstract List<SpotType> getAllowedSpotTypes();

    /**
     * Returns the type of this vehicle.
     * 
     * @return the VehicleType enum value
     */
    public abstract VehicleType getVehicleType();

    /**
     * Gets the vehicle's license plate.
     * 
     * @return the license plate string
     */
    public String getLicensePlate() {
        return licensePlate;
    }

    /**
     * Gets the entry time when the vehicle entered the parking lot.
     * 
     * @return the entry time, or null if not yet entered
     */
    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    /**
     * Sets the entry time when the vehicle enters the parking lot.
     * 
     * @param entryTime the time of entry
     */
    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    /**
     * Gets the exit time when the vehicle left the parking lot.
     * 
     * @return the exit time, or null if not yet exited
     */
    public LocalDateTime getExitTime() {
        return exitTime;
    }

    /**
     * Sets the exit time when the vehicle exits the parking lot.
     * 
     * @param exitTime the time of exit
     */
    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    /**
     * Checks if this vehicle can park in the given spot type.
     * 
     * @param spotType the spot type to check
     * @return true if the vehicle can park in this spot type
     */
    public boolean canParkIn(SpotType spotType) {
        return getAllowedSpotTypes().contains(spotType);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "licensePlate='" + licensePlate + '\'' +
                ", entryTime=" + entryTime +
                ", exitTime=" + exitTime +
                '}';
    }
}
