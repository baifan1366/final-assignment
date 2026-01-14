package com.university.parking.domain;

/**
 * Enumeration representing the status of a parking reservation.
 * Demonstrates future-proof design for reservation system extension.
 */
public enum ReservationStatus {
    /** Reservation created but not yet confirmed */
    PENDING,
    
    /** Reservation confirmed and active */
    CONFIRMED,
    
    /** Reservation cancelled by user or system */
    CANCELLED,
    
    /** Reservation completed (vehicle used the spot) */
    COMPLETED,
    
    /** Reservation expired (not used within time window) */
    EXPIRED
}
