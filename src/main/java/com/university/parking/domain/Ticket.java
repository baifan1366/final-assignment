package com.university.parking.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value object representing a parking ticket generated upon vehicle entry.
 * Requirements: 3.6
 */
public class Ticket {
    private final String ticketId;
    private final String licensePlate;
    private final String spotId;
    private final LocalDateTime entryTime;

    public Ticket(String ticketId, String licensePlate, String spotId, LocalDateTime entryTime) {
        this.ticketId = ticketId;
        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.entryTime = entryTime;
    }

    /**
     * Generates a ticket ID with format T-{PLATE}-{TIMESTAMP}.
     * @param plate the vehicle license plate
     * @return the generated ticket ID
     */
    public static String generateTicketId(String plate) {
        return "T-" + plate + "-" + System.currentTimeMillis();
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getSpotId() {
        return spotId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(ticketId, ticket.ticketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId='" + ticketId + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", spotId='" + spotId + '\'' +
                ", entryTime=" + entryTime +
                '}';
    }
}
