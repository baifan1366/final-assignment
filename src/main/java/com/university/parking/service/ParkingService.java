package com.university.parking.service;

import com.university.parking.domain.*;

import java.util.List;

/**
 * Service interface for parking operations.
 * Handles vehicle entry, exit, and parking fee calculations.
 * Requirements: 3.1-3.6, 4.1-4.7
 */
public interface ParkingService {
    
    /**
     * Gets all available parking spots for a given vehicle type.
     * 
     * @param vehicleType the type of vehicle
     * @return list of available and compatible parking spots
     */
    List<ParkingSpot> getAvailableSpots(VehicleType vehicleType);
    
    /**
     * Processes vehicle entry into the parking lot.
     * 
     * @param licensePlate the vehicle's license plate
     * @param vehicleType the type of vehicle
     * @param spotId the ID of the selected parking spot
     * @return the generated parking ticket
     * @throws IllegalArgumentException if inputs are invalid
     * @throws IllegalStateException if spot is not available or incompatible
     */
    Ticket processEntry(String licensePlate, VehicleType vehicleType, String spotId);
    
    /**
     * Processes vehicle exit from the parking lot.
     * 
     * @param licensePlate the vehicle's license plate
     * @param paymentMethod the payment method used
     * @param payFines whether to pay outstanding fines during this exit
     * @return the generated receipt
     * @throws IllegalArgumentException if license plate is not found
     */
    Receipt processExit(String licensePlate, PaymentMethod paymentMethod, boolean payFines);
    
    /**
     * Calculates the parking fee for a vehicle.
     * Uses ceiling method for duration (rounds up to next hour).
     * Applies special pricing for HandicappedVehicle.
     * 
     * @param vehicle the vehicle
     * @param spot the parking spot
     * @return the calculated parking fee
     */
    double calculateParkingFee(Vehicle vehicle, ParkingSpot spot);
    
    /**
     * Gets the current parking lot status.
     * 
     * @return the parking lot with current state
     */
    ParkingLot getParkingLotStatus();
    
    /**
     * Finds a vehicle by its license plate.
     * 
     * @param licensePlate the license plate to search for
     * @return the vehicle if found, null otherwise
     */
    Vehicle findVehicleByPlate(String licensePlate);
    
    /**
     * Finds the parking spot occupied by a vehicle.
     * 
     * @param licensePlate the vehicle's license plate
     * @return the parking spot if found, null otherwise
     */
    ParkingSpot findSpotByVehiclePlate(String licensePlate);
}
