package com.university.parking.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a floor in the parking lot.
 * Part of the Composite Pattern for parking structure.
 * Requirements: 1.2
 */
public class Floor {
    private final String floorId;
    private final int floorNumber;
    private final List<ParkingSpot> spots;

    /**
     * Creates a new Floor with the specified properties.
     *
     * @param floorId unique identifier for the floor
     * @param floorNumber the floor number (e.g., 1, 2, 3)
     */
    public Floor(String floorId, int floorNumber) {
        if (floorId == null || floorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Floor ID cannot be null or empty");
        }
        this.floorId = floorId;
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
    }

    /**
     * Adds a parking spot to this floor.
     *
     * @param spot the parking spot to add
     * @throws IllegalArgumentException if spot is null
     */
    public void addSpot(ParkingSpot spot) {
        if (spot == null) {
            throw new IllegalArgumentException("Parking spot cannot be null");
        }
        spots.add(spot);
    }

    /**
     * Gets all available parking spots on this floor.
     *
     * @return list of available parking spots
     */
    public List<ParkingSpot> getAvailableSpots() {
        return spots.stream()
                .filter(ParkingSpot::isAvailable)
                .collect(Collectors.toList());
    }


    /**
     * Gets available spots that can accommodate the given vehicle type.
     *
     * @param vehicleType the type of vehicle
     * @return list of available and compatible parking spots
     */
    public List<ParkingSpot> getAvailableSpots(VehicleType vehicleType) {
        return spots.stream()
                .filter(ParkingSpot::isAvailable)
                .filter(spot -> spot.canAccommodate(vehicleType))
                .collect(Collectors.toList());
    }

    /**
     * Calculates the occupancy rate for this floor.
     * Occupancy rate = occupied spots / total spots
     *
     * @return occupancy rate as a decimal (0.0 to 1.0), or 0.0 if no spots
     */
    public double getOccupancyRate() {
        if (spots.isEmpty()) {
            return 0.0;
        }
        long occupiedCount = spots.stream()
                .filter(spot -> !spot.isAvailable())
                .count();
        return (double) occupiedCount / spots.size();
    }

    /**
     * Gets the total number of spots on this floor.
     *
     * @return total number of spots
     */
    public int getTotalSpots() {
        return spots.size();
    }

    /**
     * Gets the number of occupied spots on this floor.
     *
     * @return number of occupied spots
     */
    public int getOccupiedSpots() {
        return (int) spots.stream()
                .filter(spot -> !spot.isAvailable())
                .count();
    }

    // Getters
    public String getFloorId() {
        return floorId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    /**
     * Gets an unmodifiable view of all spots on this floor.
     *
     * @return unmodifiable list of parking spots
     */
    public List<ParkingSpot> getSpots() {
        return Collections.unmodifiableList(spots);
    }

    @Override
    public String toString() {
        return "Floor{" +
                "floorId='" + floorId + '\'' +
                ", floorNumber=" + floorNumber +
                ", totalSpots=" + spots.size() +
                ", occupancyRate=" + String.format("%.2f", getOccupancyRate() * 100) + "%" +
                '}';
    }
}
