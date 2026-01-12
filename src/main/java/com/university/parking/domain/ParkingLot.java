package com.university.parking.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the entire parking lot containing multiple floors.
 * Part of the Composite Pattern for parking structure.
 * Requirements: 1.1, 1.5, 1.6
 */
public class ParkingLot {
    private final String lotId;
    private final String name;
    private final List<Floor> floors;

    /**
     * Creates a new ParkingLot with the specified properties.
     *
     * @param lotId unique identifier for the parking lot
     * @param name the name of the parking lot
     */
    public ParkingLot(String lotId, String name) {
        if (lotId == null || lotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lot ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.lotId = lotId;
        this.name = name;
        this.floors = new ArrayList<>();
    }

    /**
     * Adds a floor to this parking lot.
     *
     * @param floor the floor to add
     * @throws IllegalArgumentException if floor is null
     */
    public void addFloor(Floor floor) {
        if (floor == null) {
            throw new IllegalArgumentException("Floor cannot be null");
        }
        floors.add(floor);
    }

    /**
     * Gets all available parking spots that can accommodate the given vehicle type.
     * Filters by both availability and vehicle type compatibility.
     * Requirements: 1.5
     *
     * @param vehicleType the type of vehicle
     * @return list of available and compatible parking spots across all floors
     */
    public List<ParkingSpot> getAvailableSpots(VehicleType vehicleType) {
        if (vehicleType == null) {
            return Collections.emptyList();
        }
        return floors.stream()
                .flatMap(floor -> floor.getAvailableSpots(vehicleType).stream())
                .collect(Collectors.toList());
    }


    /**
     * Gets all available parking spots across all floors.
     *
     * @return list of all available parking spots
     */
    public List<ParkingSpot> getAllAvailableSpots() {
        return floors.stream()
                .flatMap(floor -> floor.getAvailableSpots().stream())
                .collect(Collectors.toList());
    }

    /**
     * Gets the total number of parking spots across all floors.
     * Requirements: 1.6
     *
     * @return total number of spots
     */
    public int getTotalSpots() {
        return floors.stream()
                .mapToInt(Floor::getTotalSpots)
                .sum();
    }

    /**
     * Gets the number of occupied parking spots across all floors.
     * Requirements: 1.6
     *
     * @return number of occupied spots
     */
    public int getOccupiedSpots() {
        return floors.stream()
                .mapToInt(Floor::getOccupiedSpots)
                .sum();
    }

    /**
     * Calculates the occupancy rate for the entire parking lot.
     * Occupancy rate = occupied spots / total spots
     * Requirements: 1.6
     *
     * @return occupancy rate as a decimal (0.0 to 1.0), or 0.0 if no spots
     */
    public double getOccupancyRate() {
        int totalSpots = getTotalSpots();
        if (totalSpots == 0) {
            return 0.0;
        }
        return (double) getOccupiedSpots() / totalSpots;
    }

    /**
     * Finds a parking spot by its ID across all floors.
     *
     * @param spotId the spot ID to search for
     * @return the parking spot, or null if not found
     */
    public ParkingSpot findSpotById(String spotId) {
        if (spotId == null) {
            return null;
        }
        return floors.stream()
                .flatMap(floor -> floor.getSpots().stream())
                .filter(spot -> spotId.equals(spot.getSpotId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a parking spot by the vehicle's license plate.
     *
     * @param licensePlate the license plate to search for
     * @return the parking spot, or null if not found
     */
    public ParkingSpot findSpotByVehiclePlate(String licensePlate) {
        if (licensePlate == null) {
            return null;
        }
        return floors.stream()
                .flatMap(floor -> floor.getSpots().stream())
                .filter(spot -> licensePlate.equals(spot.getCurrentVehiclePlate()))
                .findFirst()
                .orElse(null);
    }

    // Getters
    public String getLotId() {
        return lotId;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets an unmodifiable view of all floors in this parking lot.
     *
     * @return unmodifiable list of floors
     */
    public List<Floor> getFloors() {
        return Collections.unmodifiableList(floors);
    }

    @Override
    public String toString() {
        return "ParkingLot{" +
                "lotId='" + lotId + '\'' +
                ", name='" + name + '\'' +
                ", totalFloors=" + floors.size() +
                ", totalSpots=" + getTotalSpots() +
                ", occupancyRate=" + String.format("%.2f", getOccupancyRate() * 100) + "%" +
                '}';
    }
}
