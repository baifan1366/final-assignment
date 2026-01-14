package com.university.parking.domain;

/**
 * Enumeration representing the types of vehicles supported by the system.
 * Requirements: 2.1
 * Future-proof: Easy to add new vehicle types (e.g., BUS, ELECTRIC_VEHICLE)
 */
public enum VehicleType {
    MOTORCYCLE,
    CAR,
    SUV_TRUCK,
    HANDICAPPED,
    BUS  // Future-proof: Added for large vehicles
}
