package com.university.parking.service;

import com.university.parking.dao.ReservationDAO;
import com.university.parking.domain.Reservation;
import com.university.parking.domain.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of ReservationService.
 * Demonstrates future-proof design with complete reservation management.
 */
public class ReservationServiceImpl implements ReservationService {
    
    private final ReservationDAO reservationDAO;
    
    public ReservationServiceImpl(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }
    
    @Override
    public Reservation createReservation(String licensePlate, String spotId,
                                         LocalDateTime startTime, LocalDateTime endTime) {
        // Validate inputs
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be empty");
        }
        if (spotId == null || spotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Spot ID cannot be empty");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start and end time cannot be null");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start time cannot be in the past");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        
        // Check if spot is available for the time range
        if (!isSpotAvailableForReservation(spotId, startTime, endTime)) {
            throw new IllegalStateException("Spot is not available for the requested time range");
        }
        
        // Create and save reservation
        Reservation reservation = new Reservation(licensePlate, spotId, startTime, endTime);
        reservationDAO.save(reservation);
        
        return reservation;
    }
    
    @Override
    public void confirmReservation(String reservationId) {
        Reservation reservation = findById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }
        
        reservation.confirm();
        reservationDAO.update(reservation);
    }
    
    @Override
    public void cancelReservation(String reservationId) {
        Reservation reservation = findById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }
        
        reservation.cancel();
        reservationDAO.update(reservation);
    }
    
    @Override
    public Reservation findById(String reservationId) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            return null;
        }
        return reservationDAO.findById(reservationId);
    }
    
    @Override
    public List<Reservation> findByLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be empty");
        }
        return reservationDAO.findByLicensePlate(licensePlate);
    }
    
    @Override
    public List<Reservation> findBySpotAndTimeRange(String spotId,
                                                     LocalDateTime startTime,
                                                     LocalDateTime endTime) {
        if (spotId == null || spotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Spot ID cannot be empty");
        }
        return reservationDAO.findBySpotAndTimeRange(spotId, startTime, endTime);
    }
    
    @Override
    public boolean isSpotAvailableForReservation(String spotId,
                                                  LocalDateTime startTime,
                                                  LocalDateTime endTime) {
        List<Reservation> overlapping = findBySpotAndTimeRange(spotId, startTime, endTime);
        return overlapping.isEmpty();
    }
    
    @Override
    public boolean hasValidReservation(String licensePlate, String spotId) {
        if (licensePlate == null || spotId == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> reservations = reservationDAO.findByLicensePlate(licensePlate);
        
        return reservations.stream()
                .anyMatch(r -> r.getSpotId().equals(spotId)
                        && r.getStatus() == ReservationStatus.CONFIRMED
                        && r.getStartTime().isBefore(now.plusMinutes(30)) // Allow 30 min early
                        && r.getEndTime().isAfter(now));
    }
    
    @Override
    public List<Reservation> getActiveReservations() {
        return reservationDAO.findAllActive();
    }
    
    @Override
    public List<Reservation> getAllReservations() {
        return reservationDAO.findAll();
    }
    
    @Override
    public void processExpiredReservations() {
        List<Reservation> expired = reservationDAO.findExpiredPending();
        
        for (Reservation reservation : expired) {
            reservationDAO.updateStatus(reservation.getReservationId(), 
                    ReservationStatus.EXPIRED.name());
        }
    }
}
