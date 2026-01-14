package com.university.parking.domain;

/**
 * Factory class for creating Vehicle instances based on VehicleType.
 * Implements the Factory Pattern for vehicle creation.
 * Requirements: 2.1
 * Future-proof: Adding new vehicle types only requires adding a case here.
 */
public class VehicleFactory {

    /**
     * Creates a Vehicle instance based on the specified type and license plate.
     * 
     * @param vehicleType the type of vehicle to create
     * @param licensePlate the license plate for the vehicle
     * @return a new Vehicle instance of the appropriate subclass
     * @throws IllegalArgumentException if vehicleType is null or licensePlate is invalid
     */
    public static Vehicle createVehicle(VehicleType vehicleType, String licensePlate) {
        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be null or empty");
        }

        switch (vehicleType) {
            case MOTORCYCLE:
                return new Motorcycle(licensePlate);
            case CAR:
                return new Car(licensePlate);
            case SUV_TRUCK:
                return new SUVTruck(licensePlate);
            case HANDICAPPED:
                return new HandicappedVehicle(licensePlate);
            case BUS:
                return new Bus(licensePlate);
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + vehicleType);
        }
    }
}
