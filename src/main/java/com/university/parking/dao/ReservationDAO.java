package com.university.parking.dao;

import com.university.parking.domain.Reservation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Access Object interface for Reservation entities.
 * Demonstrates future-proof design with DAO pattern.
 */
public interface ReservationDAO extends GenericDAO<Reservation, String> {
    
    /**
     * Finds all reservations for a license plate.
     * @param licensePlate the vehicle's license plate
     * @return list of reservations
     */
    List<Reservation> findByLicensePlate(String licensePlate);
    
    /**
     * Finds all reservations for a spot within a time range.
     * @param spotId the spot ID
     * @param startTime range start
     * @param endTime range end
     * @return list of overlapping reservations
     */
    List<Reservation> findBySpotAndTimeRange(String spotId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Finds all active (confirmed and within time window) reservations.
     * @return list of active reservations
     */
    List<Reservation> findAllActive();
    
    /**
     * Finds all pending reservations that have expired.
     * @return list of expired pending reservations
     */
    List<Reservation> findExpiredPending();
    
    /**
     * Updates the status of a reservation.
     * @param reservationId the reservation ID
     * @param status the new status as string
     */
    void updateStatus(String reservationId, String status);
}
