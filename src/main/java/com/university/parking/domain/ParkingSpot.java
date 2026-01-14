package com.university.parking.domain;

/**
 * Represents a parking spot in the parking lot.
 * Part of the Composite Pattern for parking structure.
 * Requirements: 1.3, 1.4
 */
public class ParkingSpot {
    private final String spotId;
    private final SpotType type;
    private SpotStatus status;
    private final double hourlyRate;
    private String currentVehiclePlate;

    /**
     * Creates a new ParkingSpot with the specified properties.
     * Initial status is AVAILABLE as per requirement 1.3.
     *
     * @param spotId unique identifier for the spot
     * @param type the type of parking spot
     * @param hourlyRate the hourly rate for this spot
     */
    public ParkingSpot(String spotId, SpotType type, double hourlyRate) {
        if (spotId == null || spotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Spot ID cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Spot type cannot be null");
        }
        if (hourlyRate < 0) {
            throw new IllegalArgumentException("Hourly rate cannot be negative");
        }
        this.spotId = spotId;
        this.type = type;
        this.hourlyRate = hourlyRate;
        this.status = SpotStatus.AVAILABLE;
        this.currentVehiclePlate = null;
    }

    /**
     * Checks if the spot is available for parking.
     * @return true if status is AVAILABLE, false otherwise
     */
    public boolean isAvailable() {
        return status == SpotStatus.AVAILABLE;
    }

    /**
     * Checks if this spot can accommodate the given vehicle type.
     * Compatibility matrix:
     * - MOTORCYCLE: COMPACT only
     * - CAR: COMPACT, REGULAR
     * - SUV_TRUCK: REGULAR only
     * - HANDICAPPED: ALL types
     * - BUS: RESERVED only (large vehicle)
     *
     * @param vehicleType the type of vehicle to check
     * @return true if the spot can accommodate the vehicle type
     */
    public boolean canAccommodate(VehicleType vehicleType) {
        if (vehicleType == null) {
            return false;
        }
        
        switch (vehicleType) {
            case MOTORCYCLE:
                return type == SpotType.COMPACT;
            case CAR:
                return type == SpotType.COMPACT || type == SpotType.REGULAR || type == SpotType.ELECTRIC;
            case SUV_TRUCK:
                return type == SpotType.REGULAR;
            case HANDICAPPED:
                return true; // Can park in any spot type
            case BUS:
                return type == SpotType.RESERVED; // Large vehicles need reserved spots
            default:
                return false;
        }
    }


    /**
     * Assigns a vehicle to this parking spot.
     * Sets status to OCCUPIED and records the vehicle's license plate.
     *
     * @param licensePlate the license plate of the vehicle
     * @throws IllegalStateException if spot is already occupied
     * @throws IllegalArgumentException if license plate is null or empty
     */
    public void assignVehicle(String licensePlate) {
        if (!isAvailable()) {
            throw new IllegalStateException("Spot is already occupied");
        }
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be null or empty");
        }
        this.currentVehiclePlate = licensePlate;
        this.status = SpotStatus.OCCUPIED;
    }

    /**
     * Releases the vehicle from this parking spot.
     * Sets status to AVAILABLE and clears the current vehicle.
     *
     * @throws IllegalStateException if spot is not occupied
     */
    public void releaseVehicle() {
        if (isAvailable()) {
            throw new IllegalStateException("Spot is not occupied");
        }
        this.currentVehiclePlate = null;
        this.status = SpotStatus.AVAILABLE;
    }

    // Getters
    public String getSpotId() {
        return spotId;
    }

    public SpotType getType() {
        return type;
    }

    public SpotStatus getStatus() {
        return status;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public String getCurrentVehiclePlate() {
        return currentVehiclePlate;
    }

    @Override
    public String toString() {
        return "ParkingSpot{" +
                "spotId='" + spotId + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", hourlyRate=" + hourlyRate +
                ", currentVehiclePlate='" + currentVehiclePlate + '\'' +
                '}';
    }
}
