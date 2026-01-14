package com.university.parking.service;

import com.university.parking.domain.Reservation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing parking reservations.
 * Demonstrates future-proof design - this interface shows how
 * the system can be extended to support advance reservations.
 */
public interface ReservationService {
    
    /**
     * Creates a new reservation for a parking spot.
     * 
     * @param licensePlate the vehicle's license plate
     * @param spotId the spot to reserve
     * @param startTime when the reservation starts
     * @param endTime when the reservation ends
     * @return the created reservation
     * @throws IllegalArgumentException if inputs are invalid
     * @throws IllegalStateException if spot is not available for the time range
     */
    Reservation createReservation(String licensePlate, String spotId, 
                                  LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Confirms a pending reservation.
     * 
     * @param reservationId the reservation to confirm
     * @throws IllegalArgumentException if reservation not found
     * @throws IllegalStateException if reservation is not pending
     */
    void confirmReservation(String reservationId);
    
    /**
     * Cancels a reservation.
     * 
     * @param reservationId the reservation to cancel
     * @throws IllegalArgumentException if reservation not found
     */
    void cancelReservation(String reservationId);
    
    /**
     * Finds a reservation by ID.
     * 
     * @param reservationId the reservation ID
     * @return the reservation, or null if not found
     */
    Reservation findById(String reservationId);
    
    /**
     * Finds all reservations for a license plate.
     * 
     * @param licensePlate the vehicle's license plate
     * @return list of reservations
     */
    List<Reservation> findByLicensePlate(String licensePlate);
    
    /**
     * Finds all reservations for a spot within a time range.
     * 
     * @param spotId the spot ID
     * @param startTime range start
     * @param endTime range end
     * @return list of reservations that overlap with the time range
     */
    List<Reservation> findBySpotAndTimeRange(String spotId, 
                                              LocalDateTime startTime, 
                                              LocalDateTime endTime);
    
    /**
     * Checks if a spot is available for reservation in the given time range.
     * 
     * @param spotId the spot ID
     * @param startTime range start
     * @param endTime range end
     * @return true if the spot can be reserved
     */
    boolean isSpotAvailableForReservation(String spotId, 
                                          LocalDateTime startTime, 
                                          LocalDateTime endTime);
    
    /**
     * Checks if a vehicle has a valid reservation for a spot.
     * Used during entry to validate Reserved spot access.
     * 
     * @param licensePlate the vehicle's license plate
     * @param spotId the spot ID
     * @return true if the vehicle has an active reservation for this spot
     */
    boolean hasValidReservation(String licensePlate, String spotId);
    
    /**
     * Gets all active reservations.
     * 
     * @return list of confirmed reservations that are currently active
     */
    List<Reservation> getActiveReservations();
    
    /**
     * Marks expired reservations and applies no-show fines if configured.
     * Should be called periodically by a scheduler.
     */
    void processExpiredReservations();
}
