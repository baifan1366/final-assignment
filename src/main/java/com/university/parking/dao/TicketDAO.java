package com.university.parking.dao;

import com.university.parking.domain.Ticket;

import java.util.List;

/**
 * Data Access Object interface for Ticket entities.
 * Provides standard CRUD operations for parking tickets.
 * Requirements: 9.2, 9.3
 */
public interface TicketDAO extends GenericDAO<Ticket, String> {
    
    /**
     * Finds a ticket by the associated license plate.
     * 
     * @param licensePlate the vehicle's license plate
     * @return the ticket if found, null otherwise
     */
    Ticket findByLicensePlate(String licensePlate);
    
    /**
     * Finds all active tickets (vehicles currently parked).
     * 
     * @return list of active tickets
     */
    List<Ticket> findActiveTickets();
}
