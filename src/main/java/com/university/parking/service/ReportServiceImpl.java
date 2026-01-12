package com.university.parking.service;

import com.university.parking.dao.FineDAO;
import com.university.parking.dao.ParkingSpotDAO;
import com.university.parking.dao.PaymentDAO;
import com.university.parking.dao.VehicleDAO;
import com.university.parking.domain.Fine;
import com.university.parking.domain.ParkingSpot;
import com.university.parking.domain.Vehicle;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of ReportService.
 * Provides parking lot statistics and reports.
 * Requirements: 7.1-7.6
 */
public class ReportServiceImpl implements ReportService {
    
    private final ParkingSpotDAO parkingSpotDAO;
    private final VehicleDAO vehicleDAO;
    private final FineDAO fineDAO;
    private final PaymentDAO paymentDAO;
    
    public ReportServiceImpl(ParkingSpotDAO parkingSpotDAO, VehicleDAO vehicleDAO, 
                             FineDAO fineDAO, PaymentDAO paymentDAO) {
        this.parkingSpotDAO = parkingSpotDAO;
        this.vehicleDAO = vehicleDAO;
        this.fineDAO = fineDAO;
        this.paymentDAO = paymentDAO;
    }
    
    @Override
    public double getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Date range cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        // Get total revenue from payments (includes parking fees and fines)
        return paymentDAO.getTotalRevenue(startDate, endDate);
    }
    
    @Override
    public List<Vehicle> getCurrentlyParkedVehicles() {
        return vehicleDAO.findCurrentlyParked();
    }
    
    @Override
    public List<Fine> getOutstandingFines() {
        return fineDAO.findAllUnpaid();
    }
    
    @Override
    public double getOccupancyRate() {
        List<ParkingSpot> allSpots = parkingSpotDAO.findAll();
        if (allSpots.isEmpty()) {
            return 0.0;
        }
        
        long occupiedCount = allSpots.stream()
                .filter(spot -> !spot.isAvailable())
                .count();
        
        return (double) occupiedCount / allSpots.size();
    }
    
    @Override
    public int getTotalSpots() {
        return parkingSpotDAO.findAll().size();
    }
    
    @Override
    public int getAvailableSpots() {
        return parkingSpotDAO.findAllAvailable().size();
    }
    
    @Override
    public List<ParkingSpot> getAllSpots() {
        return parkingSpotDAO.findAll();
    }
}
