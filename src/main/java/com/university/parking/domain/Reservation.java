package com.university.parking.domain;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a parking spot reservation.
 * Demonstrates future-proof design - customers can reserve spots in advance.
 * This class shows how the system can be extended without major refactoring.
 */
public class Reservation {
    
    private final String reservationId;
    private final String licensePlate;
    private final String spotId;
    private final LocalDateTime reservationTime;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private ReservationStatus status;
    
    /**
     * Creates a new reservation.
     * 
     * @param licensePlate the vehicle's license plate
     * @param spotId the reserved spot ID
     * @param startTime when the reservation starts
     * @param endTime when the reservation ends
     */
    public Reservation(String licensePlate, String spotId, 
                       LocalDateTime startTime, LocalDateTime endTime) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be empty");
        }
        if (spotId == null || spotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Spot ID cannot be empty");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start and end time cannot be null");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        
        this.reservationId = generateReservationId();
        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.reservationTime = LocalDateTime.now();
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = ReservationStatus.PENDING;
    }
    
    /**
     * Creates a reservation with a specific ID (used when loading from database).
     * 
     * @param reservationId the reservation ID
     * @param licensePlate the vehicle's license plate
     * @param spotId the reserved spot ID
     * @param reservationTime when the reservation was created
     * @param startTime when the reservation starts
     * @param endTime when the reservation ends
     * @param status the reservation status
     */
    public Reservation(String reservationId, String licensePlate, String spotId,
                       LocalDateTime reservationTime, LocalDateTime startTime, 
                       LocalDateTime endTime, ReservationStatus status) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reservation ID cannot be empty");
        }
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be empty");
        }
        if (spotId == null || spotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Spot ID cannot be empty");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start and end time cannot be null");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        
        this.reservationId = reservationId;
        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.reservationTime = reservationTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
    
    /**
     * Generates a unique reservation ID.
     */
    private String generateReservationId() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Checks if the reservation is currently active.
     */
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == ReservationStatus.CONFIRMED 
            && now.isAfter(startTime) 
            && now.isBefore(endTime);
    }
    
    /**
     * Checks if the reservation overlaps with a given time range.
     */
    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return startTime.isBefore(otherEnd) && endTime.isAfter(otherStart);
    }
    
    /**
     * Confirms the reservation.
     */
    public void confirm() {
        if (status != ReservationStatus.PENDING) {
            throw new IllegalStateException("Can only confirm pending reservations");
        }
        this.status = ReservationStatus.CONFIRMED;
    }
    
    /**
     * Cancels the reservation.
     */
    public void cancel() {
        if (status == ReservationStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed reservations");
        }
        this.status = ReservationStatus.CANCELLED;
    }
    
    /**
     * Marks the reservation as completed (vehicle has used the spot).
     */
    public void complete() {
        if (status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Can only complete confirmed reservations");
        }
        this.status = ReservationStatus.COMPLETED;
    }
    
    /**
     * Checks if the reservation has expired (past end time without being used).
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endTime) 
            && status != ReservationStatus.COMPLETED;
    }

    // Getters
    
    public String getReservationId() {
        return reservationId;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public String getSpotId() {
        return spotId;
    }
    
    public LocalDateTime getReservationTime() {
        return reservationTime;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public ReservationStatus getStatus() {
        return status;
    }
    
    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", spotId='" + spotId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                '}';
    }
}
