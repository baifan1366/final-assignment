package com.university.parking.service;

import com.university.parking.dao.*;
import com.university.parking.domain.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ParkingService.
 * Handles vehicle entry, exit, and parking fee calculations.
 * Requirements: 3.1-3.6, 4.1-4.7
 */
public class ParkingServiceImpl implements ParkingService {
    
    private static final int OVERSTAY_THRESHOLD_HOURS = 24;
    private static final double RESERVED_SPOT_VIOLATION_FINE = 100.0;
    
    private final ParkingSpotDAO parkingSpotDAO;
    private final VehicleDAO vehicleDAO;
    private final TicketDAO ticketDAO;
    private final FineDAO fineDAO;
    private final PaymentDAO paymentDAO;
    private FineService fineService;
    private ReservationService reservationService;
    
    public ParkingServiceImpl(ParkingSpotDAO parkingSpotDAO, VehicleDAO vehicleDAO, 
                              TicketDAO ticketDAO, FineDAO fineDAO, PaymentDAO paymentDAO) {
        this.parkingSpotDAO = parkingSpotDAO;
        this.vehicleDAO = vehicleDAO;
        this.ticketDAO = ticketDAO;
        this.fineDAO = fineDAO;
        this.paymentDAO = paymentDAO;
    }
    
    /**
     * Sets the fine service for fine calculation.
     * @param fineService the fine service
     */
    public void setFineService(FineService fineService) {
        this.fineService = fineService;
    }
    
    /**
     * Sets the reservation service for reservation validation.
     * @param reservationService the reservation service
     */
    public void setReservationService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    
    @Override
    public List<ParkingSpot> getAvailableSpots(VehicleType vehicleType) {
        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }
        
        // Get all available spots and filter by vehicle type compatibility
        List<ParkingSpot> allAvailable = parkingSpotDAO.findAllAvailable();
        return allAvailable.stream()
                .filter(spot -> spot.canAccommodate(vehicleType))
                .collect(Collectors.toList());
    }
    
    @Override
    public Ticket processEntry(String licensePlate, VehicleType vehicleType, String spotId) {
        // Validate inputs (Requirements 3.7)
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be empty");
        }
        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type must be selected");
        }
        if (spotId == null || spotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Spot must be selected");
        }
        
        // Check if vehicle is already parked (indicates they left without proper exit)
        Vehicle activeVehicle = vehicleDAO.findActiveByLicensePlate(licensePlate);
        if (activeVehicle != null) {
            // Vehicle is already parked - they are trying to park again without exiting properly
            LocalDateTime entryTime = activeVehicle.getEntryTime();
            if (entryTime != null) {
                long totalHours = ChronoUnit.HOURS.between(entryTime, LocalDateTime.now());
                
                if (totalHours > OVERSTAY_THRESHOLD_HOURS) {
                    // Issue overstay fine - they escaped without paying for previous parking
                    int overstayHours = (int) (totalHours - OVERSTAY_THRESHOLD_HOURS);
                    
                    if (fineService != null) {
                        double fineAmount = fineService.calculateFine(overstayHours);
                        Fine overstayFine = new Fine(licensePlate, fineAmount, 
                            "Overstay violation - vehicle escaped after parking for " + totalHours + 
                            " hours, exceeded limit by " + overstayHours + " hours");
                        fineDAO.save(overstayFine);
                    }
                }
                
                // Clean up the old parking record (they escaped, so mark as exited)
                ParkingSpot oldSpot = parkingSpotDAO.findByVehiclePlate(licensePlate);
                if (oldSpot != null) {
                    oldSpot.releaseVehicle();
                    parkingSpotDAO.update(oldSpot);
                }
                activeVehicle.setExitTime(LocalDateTime.now());
                vehicleDAO.update(activeVehicle);
            }
        }
        
        // Find and validate the spot (Requirements 3.4)
        ParkingSpot spot = parkingSpotDAO.findById(spotId);
        if (spot == null) {
            throw new IllegalStateException("Spot not found: " + spotId);
        }
        if (!spot.isAvailable()) {
            throw new IllegalStateException("Spot is not available: " + spotId);
        }
        if (!spot.canAccommodate(vehicleType)) {
            throw new IllegalStateException("Spot is not compatible with vehicle type: " + vehicleType);
        }
        
        // Check for Reserved spot - issue fine but allow parking
        if (spot.getType() == SpotType.RESERVED && vehicleType != VehicleType.HANDICAPPED) {
            boolean hasReservation = reservationService != null 
                && reservationService.hasValidReservation(licensePlate, spotId);
            
            if (!hasReservation && fineService != null) {
                // Issue fine for parking without reservation, but allow them to park
                Fine reservedFine = new Fine(licensePlate, RESERVED_SPOT_VIOLATION_FINE, 
                    "Reserved spot violation - parked without reservation");
                fineDAO.save(reservedFine);
            }
        }
        // Note: RESERVED spots can be used by anyone, but non-reserved vehicles will be fined
        // The fine will be issued during exit in checkAndIssueFines()
        
        // Create vehicle and set entry time (Requirements 3.5)
        LocalDateTime entryTime = LocalDateTime.now();
        Vehicle vehicle = VehicleFactory.createVehicle(vehicleType, licensePlate);
        vehicle.setEntryTime(entryTime);
        
        // Assign vehicle to spot
        spot.assignVehicle(licensePlate);
        
        // Generate ticket (Requirements 3.6)
        String ticketId = Ticket.generateTicketId(licensePlate);
        Ticket ticket = new Ticket(ticketId, licensePlate, spotId, entryTime);
        
        // Persist to database
        vehicleDAO.save(vehicle);
        parkingSpotDAO.update(spot);
        ticketDAO.save(ticket);
        
        return ticket;
    }
    
    @Override
    public Receipt processExit(String licensePlate, PaymentMethod paymentMethod, boolean payFines) {
        // Validate input (Requirements 4.8)
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be empty");
        }
        
        // Find the vehicle (Requirements 4.1)
        Vehicle vehicle = vehicleDAO.findActiveByLicensePlate(licensePlate);
        if (vehicle == null || vehicle.getEntryTime() == null || vehicle.getExitTime() != null) {
            throw new IllegalArgumentException("Vehicle not found in parked vehicles: " + licensePlate);
        }
        
        // Find the parking spot
        ParkingSpot spot = parkingSpotDAO.findByVehiclePlate(licensePlate);
        if (spot == null) {
            throw new IllegalStateException("Parking spot not found for vehicle: " + licensePlate);
        }
        
        // Set exit time
        LocalDateTime exitTime = LocalDateTime.now();
        vehicle.setExitTime(exitTime);
        
        // Check for overstay fine (more than 24 hours)
        checkAndIssueFines(vehicle, spot, licensePlate);
        
        // Calculate parking fee (Requirements 4.2, 4.3)
        double parkingFee = calculateParkingFee(vehicle, spot);
        
        // Get unpaid fines (Requirements 4.4) - MUST pay all fines at exit
        double fineAmount = fineDAO.sumUnpaidByLicensePlate(licensePlate);
        
        // Calculate total amount - always include fines (no choice to skip)
        double totalAmount = parkingFee + fineAmount;
        
        // Process payment - always pay parking fee + all fines
        Ticket ticket = ticketDAO.findByLicensePlate(licensePlate);
        String ticketId = ticket != null ? ticket.getTicketId() : null;
        Payment payment = new Payment(totalAmount, paymentMethod, licensePlate, ticketId);
        paymentDAO.save(payment);
        
        // Mark all fines as paid
        List<Fine> unpaidFines = fineDAO.findUnpaidByLicensePlate(licensePlate);
        for (Fine fine : unpaidFines) {
            fineDAO.markAsPaid(fine.getFineId());
        }
        
        // CRITICAL: Complete reservation if this was a reserved spot
        if (reservationService != null && spot.getType() == SpotType.RESERVED) {
            try {
                List<Reservation> activeReservations = reservationService
                    .findByLicensePlate(licensePlate).stream()
                    .filter(r -> r.getSpotId().equals(spot.getSpotId()))
                    .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                    .filter(r -> r.isActive() || 
                           (r.getStartTime().isBefore(exitTime) && r.getEndTime().isAfter(vehicle.getEntryTime())))
                    .collect(Collectors.toList());
                
                // Complete the reservation
                for (Reservation res : activeReservations) {
                    res.complete();
                    // Note: Need to add update method to ReservationService
                    // For now, we'll use the DAO directly if available
                    System.out.println("Completing reservation: " + res.getReservationId());
                }
            } catch (Exception e) {
                // Log error but don't fail the exit process
                System.err.println("Error completing reservation: " + e.getMessage());
            }
        }
        
        // Release the spot (Requirements 4.6)
        spot.releaseVehicle();
        parkingSpotDAO.update(spot);
        
        // Update vehicle record
        vehicleDAO.update(vehicle);
        
        // Calculate duration for receipt
        LocalDateTime entryTime = vehicle.getEntryTime();
        long minutes = ChronoUnit.MINUTES.between(entryTime, exitTime);
        int durationHours = (int) Math.ceil(minutes / 60.0);
        if (durationHours < 1) durationHours = 1;
        
        // Get hourly rate (considering handicapped discount)
        double hourlyRate;
        if (vehicle instanceof HandicappedVehicle) {
            hourlyRate = ((HandicappedVehicle) vehicle).getEffectiveHourlyRate(spot);
        } else {
            hourlyRate = spot.getHourlyRate();
        }
        
        // Generate receipt (Requirements 4.7, 6.4) - always show fines paid
        String receiptId = "R-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Receipt(receiptId, licensePlate, entryTime, exitTime, 
                          durationHours, hourlyRate, parkingFee, fineAmount, paymentMethod);
    }
    
    @Override
    public double calculateParkingFee(Vehicle vehicle, ParkingSpot spot) {
        if (vehicle == null || spot == null) {
            return 0.0;
        }
        
        LocalDateTime entryTime = vehicle.getEntryTime();
        LocalDateTime exitTime = vehicle.getExitTime() != null ? vehicle.getExitTime() : LocalDateTime.now();
        
        if (entryTime == null) {
            return 0.0;
        }
        
        // Calculate duration using ceiling method (Requirements 4.2)
        long minutes = ChronoUnit.MINUTES.between(entryTime, exitTime);
        int hours = (int) Math.ceil(minutes / 60.0);
        if (hours < 1) {
            hours = 1; // Minimum 1 hour
        }
        
        // Apply special pricing for HandicappedVehicle (Requirements 2.5, 2.6)
        double hourlyRate;
        if (vehicle instanceof HandicappedVehicle) {
            HandicappedVehicle handicappedVehicle = (HandicappedVehicle) vehicle;
            hourlyRate = handicappedVehicle.getEffectiveHourlyRate(spot);
        } else {
            hourlyRate = spot.getHourlyRate();
        }
        
        return hours * hourlyRate;
    }
    
    @Override
    public ParkingLot getParkingLotStatus() {
        // Build parking lot from database
        ParkingLot parkingLot = new ParkingLot("LOT-001", "University Parking Lot");
        
        // Get all spots and group by floor
        List<ParkingSpot> allSpots = parkingSpotDAO.findAll();
        
        // Group spots by floor ID and create floors
        allSpots.stream()
                .collect(Collectors.groupingBy(spot -> extractFloorId(spot.getSpotId())))
                .forEach((floorId, spots) -> {
                    int floorNumber = extractFloorNumber(floorId);
                    Floor floor = new Floor(floorId, floorNumber);
                    spots.forEach(floor::addSpot);
                    parkingLot.addFloor(floor);
                });
        
        return parkingLot;
    }
    
    @Override
    public Vehicle findVehicleByPlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            return null;
        }
        return vehicleDAO.findActiveByLicensePlate(licensePlate);
    }
    
    @Override
    public ParkingSpot findSpotByVehiclePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            return null;
        }
        return parkingSpotDAO.findByVehiclePlate(licensePlate);
    }

    @Override
    public List<ParkingSpot> getAllSpots() {
        return parkingSpotDAO.findAll();
    }

    
    /**
     * Extracts floor ID from spot ID (assumes format like "F1-S01").
     */
    private String extractFloorId(String spotId) {
        if (spotId != null && spotId.contains("-")) {
            return spotId.substring(0, spotId.indexOf("-"));
        }
        return "F1";
    }
    
    /**
     * Extracts floor number from floor ID (assumes format like "F1").
     */
    private int extractFloorNumber(String floorId) {
        if (floorId != null && floorId.startsWith("F")) {
            try {
                return Integer.parseInt(floorId.substring(1));
            } catch (NumberFormatException e) {
                return 1;
            }
        }
        return 1;
    }
    
    /**
     * Checks for fine conditions and issues fines if applicable.
     * This method is called during exit to check for any remaining violations.
     * Note: Most fines are now issued at entry time (overstay on re-entry, reserved spot violation).
     */
    private void checkAndIssueFines(Vehicle vehicle, ParkingSpot spot, String licensePlate) {
        LocalDateTime entryTime = vehicle.getEntryTime();
        LocalDateTime exitTime = vehicle.getExitTime();
        
        if (entryTime == null || exitTime == null) {
            return;
        }
        
        long totalHours = ChronoUnit.HOURS.between(entryTime, exitTime);
        
        // Check for overstay at exit (more than 24 hours) - backup check
        // Primary overstay fine is issued when attempting to re-park
        if (totalHours > OVERSTAY_THRESHOLD_HOURS) {
            // Check if fine was already issued for this vehicle
            List<Fine> existingFines = fineDAO.findUnpaidByLicensePlate(licensePlate);
            boolean alreadyFined = existingFines.stream()
                .anyMatch(f -> f.getReason().contains("Overstay violation"));
            
            if (!alreadyFined && fineService != null) {
                int overstayHours = (int) (totalHours - OVERSTAY_THRESHOLD_HOURS);
                double fineAmount = fineService.calculateFine(overstayHours);
                Fine overstayFine = new Fine(licensePlate, fineAmount, 
                    "Overstay violation - exceeded 24 hours by " + overstayHours + " hours");
                fineDAO.save(overstayFine);
            } else {
                throw new IllegalStateException("FineService is not configured. Cannot calculate overstay fine.");
            }
        }
        
        // Check for Reserved spot violation
        // Fine is issued if vehicle parks in RESERVED spot without a valid reservation
        // This applies both before and during the reservation time period
        if (spot.getType() == SpotType.RESERVED) {
            // Check if vehicle has a valid reservation for this spot
            boolean hasReservation = reservationService != null 
                && reservationService.hasValidReservation(licensePlate, spot.getSpotId());
            
            if (!hasReservation && fineService != null) {
                // Use a fixed fine for reserved spot violation (not time-based)
                Fine reservedFine = new Fine(licensePlate, RESERVED_SPOT_VIOLATION_FINE, 
                    "Reserved spot violation - no reservation");
                fineDAO.save(reservedFine);
            }
        }
    }
}
