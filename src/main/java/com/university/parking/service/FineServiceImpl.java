package com.university.parking.service;

import com.university.parking.dao.FineDAO;
import com.university.parking.domain.Fine;
import com.university.parking.domain.FineManager;
import com.university.parking.domain.FineStrategy;

import java.util.List;

/**
 * Implementation of FineService.
 * Handles fine strategy configuration, calculation, and tracking.
 * Requirements: 5.1-5.6
 */
public class FineServiceImpl implements FineService {
    
    private final FineDAO fineDAO;
    private final FineManager fineManager;
    
    public FineServiceImpl(FineDAO fineDAO) {
        this.fineDAO = fineDAO;
        this.fineManager = new FineManager();
    }
    
    public FineServiceImpl(FineDAO fineDAO, FineManager fineManager) {
        this.fineDAO = fineDAO;
        this.fineManager = fineManager;
    }
    
    @Override
    public void setFineStrategy(FineStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Fine strategy cannot be null");
        }
        fineManager.setStrategy(strategy);
    }
    
    @Override
    public FineStrategy getCurrentStrategy() {
        return fineManager.getCurrentStrategy();
    }
    
    @Override
    public double calculateFine(int overtimeDuration) {
        if (overtimeDuration < 0) {
            return 0.0;
        }
        return fineManager.calculateFine(overtimeDuration);
    }
    
    @Override
    public Fine issueFine(String licensePlate, double amount, String reason) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be empty");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Fine amount cannot be negative");
        }
        
        Fine fine = new Fine(licensePlate, amount, reason);
        fineDAO.save(fine);
        fineManager.addFine(fine);
        
        return fine;
    }
    
    @Override
    public List<Fine> getUnpaidFines(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate cannot be empty");
        }
        return fineDAO.findUnpaidByLicensePlate(licensePlate);
    }
    
    @Override
    public double getTotalUnpaidAmount(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            return 0.0;
        }
        return fineDAO.sumUnpaidByLicensePlate(licensePlate);
    }
    
    @Override
    public void markFineAsPaid(String fineId) {
        if (fineId == null || fineId.trim().isEmpty()) {
            throw new IllegalArgumentException("Fine ID cannot be empty");
        }
        fineDAO.markAsPaid(fineId);
        fineManager.markFineAsPaid(fineId);
    }
    
    @Override
    public List<Fine> getAllUnpaidFines() {
        return fineDAO.findAllUnpaid();
    }
}
