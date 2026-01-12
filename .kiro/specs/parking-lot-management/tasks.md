# Implementation Plan: Parking Lot Management System

## Overview

本实现计划将停车场管理系统分解为增量式的编码任务，从基础设施搭建开始，逐步构建领域模型、数据访问层、业务服务层，最后完成 UI 层。每个任务都可独立验证，确保渐进式开发。

## Tasks

- [x] 1. Project Setup and Database Infrastructure
  - [x] 1.1 Create Maven/Gradle project structure with required dependencies
    - Add JUnit 5, jqwik, SQLite JDBC, Mockito dependencies
    - Configure project for Java 11+
    - _Requirements: 9.1, 9.2_
  - [x] 1.2 Implement DatabaseManager for SQLite connection management
    - Create singleton DatabaseManager class
    - Implement getConnection(), initializeDatabase(), closeConnection()
    - Create all tables (parking_spot, vehicle, ticket, fine, payment)
    - _Requirements: 9.3, 9.4_
  - [ ]* 1.3 Write unit tests for DatabaseManager
    - Test connection creation and table initialization
    - _Requirements: 9.4_

- [x] 2. Domain Layer - Enumerations and Value Objects
  - [x] 2.1 Create enumeration types
    - SpotType (COMPACT, REGULAR, HANDICAPPED, RESERVED)
    - SpotStatus (AVAILABLE, OCCUPIED)
    - VehicleType (MOTORCYCLE, CAR, SUV_TRUCK, HANDICAPPED)
    - PaymentMethod (CASH, CARD)
    - _Requirements: 1.4, 2.1, 6.1_
  - [x] 2.2 Implement Ticket value object
    - Fields: ticketId, licensePlate, spotId, entryTime
    - Static method generateTicketId(plate)
    - _Requirements: 3.6_
  - [ ]* 2.3 Write property test for Ticket format
    - **Property 8: Ticket Format Consistency**
    - **Validates: Requirements 3.6**
  - [x] 2.4 Implement Receipt value object
    - Fields: receiptId, licensePlate, parkingFee, fineAmount, totalAmount, paymentMethod, timestamp
    - _Requirements: 6.4_
  - [ ]* 2.5 Write property test for Receipt total calculation
    - **Property 15: Receipt Completeness and Total Calculation**
    - **Validates: Requirements 6.4**

- [x] 3. Checkpoint - Verify enumerations and value objects
  - Ensure all tests pass, ask the user if questions arise.

- [x] 4. Domain Layer - Parking Structure (Composite Pattern)
  - [x] 4.1 Implement ParkingSpot class
    - Fields: spotId, type, status, hourlyRate, currentVehicle
    - Methods: isAvailable(), canAccommodate(Vehicle), assignVehicle(Vehicle), releaseVehicle()
    - _Requirements: 1.3, 1.4_
  - [x] 4.2 Implement Floor class
    - Fields: floorId, floorNumber, spots (List)
    - Methods: addSpot(), getAvailableSpots(), getOccupancyRate()
    - _Requirements: 1.2_
  - [x] 4.3 Implement ParkingLot class
    - Fields: lotId, name, floors (List)
    - Methods: addFloor(), getAvailableSpots(VehicleType), getTotalSpots(), getOccupiedSpots(), getOccupancyRate()
    - _Requirements: 1.1, 1.5, 1.6_
  - [ ]* 4.4 Write property tests for parking structure
    - **Property 1: Parking Structure Invariants**
    - **Property 2: Spot Availability Filtering**
    - **Property 3: Occupancy Rate Calculation**
    - **Validates: Requirements 1.1, 1.2, 1.3, 1.4, 1.5, 1.6**

- [x] 5. Domain Layer - Vehicle Hierarchy (Inheritance)
  - [x] 5.1 Implement abstract Vehicle class
    - Fields: licensePlate, entryTime, exitTime
    - Abstract method: getAllowedSpotTypes()
    - _Requirements: 2.2, 2.3_
  - [x] 5.2 Implement Vehicle subclasses
    - Motorcycle: allows COMPACT, REGULAR
    - Car: allows COMPACT, REGULAR
    - SUVTruck: allows REGULAR only
    - HandicappedVehicle: allows ALL types
    - _Requirements: 2.1, 2.3, 2.4_
  - [x] 5.3 Implement VehicleFactory for creating vehicles by type
    - Static method createVehicle(VehicleType, licensePlate)
    - _Requirements: 2.1_
  - [ ]* 5.4 Write property tests for vehicle compatibility
    - **Property 4: Vehicle License Plate Requirement**
    - **Property 5: Vehicle Spot Type Compatibility**
    - **Validates: Requirements 2.2, 2.3**

- [x] 6. Domain Layer - Fine and Strategy Pattern
  - [x] 6.1 Implement FineStrategy interface
    - Methods: calculateFine(duration), getMaxCap()
    - _Requirements: 5.1_
  - [x] 6.2 Implement FixedFineStrategy
    - Fixed amount regardless of duration, with maxCap
    - _Requirements: 5.1, 5.5_
  - [x] 6.3 Implement ProgressiveFineStrategy
    - Base amount + increment per hour, with maxCap
    - _Requirements: 5.1, 5.5_
  - [x] 6.4 Implement HourlyFineStrategy
    - Hourly rate calculation, with maxCap
    - _Requirements: 5.1, 5.5_
  - [x] 6.5 Implement Fine entity class
    - Fields: fineId, licensePlate, amount, reason, issuedTime, paid
    - _Requirements: 5.3_
  - [x] 6.6 Implement FineManager class
    - Fields: currentStrategy
    - Methods: setStrategy(), calculateFine(), getUnpaidFines(), getTotalUnpaidAmount()
    - _Requirements: 5.2, 5.6_
  - [ ]* 6.7 Write property tests for fine calculation
    - **Property 11: Fine Strategy Application**
    - **Property 13: Fine Maximum Cap**
    - **Validates: Requirements 5.2, 5.5**

- [x] 7. Checkpoint - Verify domain layer
  - Ensure all tests pass, ask the user if questions arise.

- [x] 8. DAO Layer Implementation
  - [x] 8.1 Implement GenericDAO interface
    - Methods: findById(), findAll(), save(), update(), delete()
    - _Requirements: 9.2_
  - [x] 8.2 Implement ParkingSpotDAO
    - Additional methods: findAvailableByType(), findByVehiclePlate(), updateStatus()
    - _Requirements: 9.2, 9.3_
  - [x] 8.3 Implement VehicleDAO
    - Additional methods: findByLicensePlate(), findCurrentlyParked()
    - _Requirements: 9.2, 9.3_
  - [x] 8.4 Implement TicketDAO
    - Standard CRUD operations
    - _Requirements: 9.2, 9.3_
  - [x] 8.5 Implement FineDAO
    - Additional methods: findUnpaidByLicensePlate(), sumUnpaidByLicensePlate()
    - _Requirements: 9.2, 9.3_
  - [x] 8.6 Implement PaymentDAO
    - Standard CRUD operations
    - _Requirements: 9.2, 9.3_
  - [ ]* 8.7 Write unit tests for DAO layer
    - Test CRUD operations with in-memory SQLite
    - _Requirements: 9.2, 9.3_

- [x] 9. Service Layer Implementation
  - [x] 9.1 Implement ParkingService
    - Methods: getAvailableSpots(), processEntry(), processExit(), calculateParkingFee()
    - Integrate with ParkingSpotDAO, VehicleDAO, TicketDAO
    - _Requirements: 3.1-3.6, 4.1-4.7_
  - [ ]* 9.2 Write property tests for parking fee calculation
    - **Property 9: Parking Fee Calculation**
    - **Validates: Requirements 4.2, 4.3**
  - [ ]* 9.3 Write property tests for spot assignment
    - **Property 7: Spot Assignment Validation**
    - **Property 10: Spot Release on Exit**
    - **Validates: Requirements 3.4, 3.5, 4.6**
  - [x] 9.4 Implement FineService
    - Methods: setFineStrategy(), calculateFine(), getUnpaidFines(), markFineAsPaid()
    - Integrate with FineDAO, FineManager
    - _Requirements: 5.1-5.6_
  - [ ]* 9.5 Write property tests for fine persistence
    - **Property 12: Fine Persistence and Aggregation**
    - **Validates: Requirements 5.3, 5.4, 5.6**
  - [x] 9.6 Implement PaymentService
    - Methods: processPayment(), generateReceipt()
    - Integrate with PaymentDAO
    - _Requirements: 6.1-6.5_
  - [ ]* 9.7 Write property tests for payment handling
    - **Property 14: Payment Recording**
    - **Property 16: Payment Failure Handling**
    - **Validates: Requirements 6.2, 6.5**
  - [x] 9.8 Implement ReportService
    - Methods: getTotalRevenue(), getCurrentlyParkedVehicles(), getOutstandingFines(), getOccupancyRate()
    - _Requirements: 7.1-7.6_
  - [x] 9.9 Implement HandicappedVehicle pricing logic in ParkingService
    - Zero rate for Handicapped spot, RM2/hour for others
    - _Requirements: 2.5, 2.6_
  - [ ]* 9.10 Write property test for HandicappedVehicle pricing
    - **Property 6: HandicappedVehicle Pricing Rules**
    - **Validates: Requirements 2.5, 2.6**

- [x] 10. Checkpoint - Verify service layer
  - Ensure all tests pass, ask the user if questions arise.

- [x] 11. UI Layer - Main Frame and Navigation
  - [x] 11.1 Implement MainFrame with BorderLayout
    - NORTH: Title label "University Parking Lot Management System"
    - CENTER: JTabbedPane for navigation
    - _Requirements: 8.1, 8.2_
  - [x] 11.2 Create tab structure
    - Entry/Exit tab, Admin tab, Reports tab
    - _Requirements: 8.2_

- [x] 12. UI Layer - Entry/Exit Panel
  - [x] 12.1 Implement EntryExitPanel with GridLayout(1,2)
    - Left: VehicleEntryPanel
    - Right: VehicleExitPanel
    - _Requirements: 8.3_
  - [x] 12.2 Implement VehicleEntryPanel
    - JTextField for license plate
    - JComboBox for vehicle type
    - JTable for available spots
    - JButton "Park Vehicle"
    - Input validation with JOptionPane
    - _Requirements: 3.1-3.8, 8.4, 8.5, 8.7_
  - [x] 12.3 Implement VehicleExitPanel
    - JTextField for license plate
    - JButton "Find Vehicle"
    - Labels for parking summary (hours, fee, fines)
    - JComboBox for payment method
    - JButton "Pay & Exit"
    - _Requirements: 4.1-4.8, 8.5, 8.7_
  - [x] 12.4 Wire EntryExitPanel to ParkingService and PaymentService
    - Connect UI actions to service methods
    - Update UI based on service responses
    - _Requirements: 8.9_

- [x] 13. UI Layer - Admin Panel
  - [x] 13.1 Implement AdminPanel with vertical layout
    - Parking Lot Overview section
    - Fine Scheme Selection section
    - Statistics section
    - _Requirements: 7.1, 7.2, 7.6_
  - [x] 13.2 Implement Parking Lot Overview
    - JTable showing floors, spots, status
    - JLabel for occupancy percentage
    - _Requirements: 7.1, 7.2_
  - [x] 13.3 Implement Fine Scheme Selection
    - JRadioButton group for Fixed/Progressive/Hourly
    - JButton "Apply"
    - _Requirements: 5.2, 8.6_
  - [x] 13.4 Wire AdminPanel to FineService and ReportService
    - Connect UI actions to service methods
    - _Requirements: 8.9_

- [x] 14. UI Layer - Report Panel
  - [x] 14.1 Implement ReportPanel with JTabbedPane
    - Currently Parked Vehicles tab
    - Revenue Report tab
    - Occupancy Report tab
    - Outstanding Fines tab
    - _Requirements: 7.3, 7.4, 7.5_
  - [x] 14.2 Implement report tables
    - JTable for each report type
    - Refresh buttons for each tab
    - _Requirements: 8.4_
  - [x] 14.3 Wire ReportPanel to ReportService
    - Connect UI to service methods
    - _Requirements: 8.9_

- [x] 15. Checkpoint - Verify UI layer
  - Ensure all tests pass, ask the user if questions arise.

- [x] 16. Integration and Final Testing
  - [x] 16.1 Implement Application entry point
    - Main class to initialize database and launch MainFrame
    - _Requirements: 9.4_
  - [x] 16.2 Initialize sample data
    - Create sample floors and spots
    - _Requirements: 1.1, 1.2, 1.3_
  - [ ]* 16.3 Write integration tests for complete flows
    - Test entry → exit flow
    - Test fine accumulation across sessions
    - Test strategy switching
    - _Requirements: 3.1-3.6, 4.1-4.7, 5.2_

- [x] 17. Final Checkpoint
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Property tests validate universal correctness properties using jqwik
- Unit tests validate specific examples and edge cases
- UI layer is tested manually; business logic is tested automatically
