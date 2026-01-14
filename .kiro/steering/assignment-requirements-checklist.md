# Assignment Requirements Checklist

This steering file tracks the alignment between the Final Assignment requirements and the current implementation.

## Feature Fulfillment (20 marks)

### Feature 1: Parking Lot Structure Management ✅
| Requirement | Status | Implementation |
|-------------|--------|----------------|
| Multiple floors (e.g., 5 floors) | ✅ | `Floor.java`, `ParkingLot.java` - 5 floors (F1-F5) |
| Multiple parking spots per floor | ✅ | `Application.java` - Sample data creates spots per floor |
| Spot types: Compact, Regular, Handicapped, Reserved | ✅ | `SpotType.java` enum |
| Compact: RM2/hour | ✅ | `Application.java` - hourlyRate = 2.0 |
| Regular: RM5/hour | ✅ | `Application.java` - hourlyRate = 5.0 |
| Handicapped: RM2/hour (FREE for handicapped card holder) | ✅ | `HandicappedVehicle.getEffectiveHourlyRate()` returns 0 for handicapped spots |
| Reserved: RM10/hour | ✅ | `Application.java` - hourlyRate = 10.0 |
| Spot ID format (e.g., "F1-R1-S1") | ✅ | Format: "F1-R1-S1" (Floor-Row-Spot) |
| Spot has: ID, Type, Status, Current Vehicle, Hourly Rate | ✅ | `ParkingSpot.java` |

### Feature 2: Vehicle Entry Process ✅
| Requirement | Status | Implementation |
|-------------|--------|----------------|
| Vehicle types: Motorcycle, Car, SUV/Truck, Handicapped | ✅ | `VehicleType.java`, Vehicle subclasses |
| Motorcycle parks in Compact only | ✅ | `Motorcycle.getAllowedSpotTypes()` |
| Car parks in Compact or Regular | ✅ | `Car.getAllowedSpotTypes()` |
| SUV/Truck parks in Regular only | ✅ | `SUVTruck.getAllowedSpotTypes()` |
| Handicapped parks anywhere, RM2/hour discount | ✅ | `HandicappedVehicle.java` |
| Show available spots of suitable types | ✅ | `EntryExitPanel`, `ParkingServiceImpl.getAvailableSpots()` |
| User selects a spot | ✅ | `EntryExitPanel` - JTable selection |
| Mark spot as occupied | ✅ | `ParkingSpot.assignVehicle()` |
| Record entry time | ✅ | `Vehicle.setEntryTime()` |
| Generate ticket (T-PLATE-TIMESTAMP) | ✅ | `Ticket.generateTicketId()` |
| Display ticket with spot location and entry time | ✅ | `EntryExitPanel.handleParkVehicle()` - success dialog |

### Feature 3: Vehicle Exit and Billing Process ✅
| Requirement | Status | Implementation |
|-------------|--------|----------------|
| Enter license plate to find vehicle | ✅ | `EntryExitPanel.handleFindVehicle()` |
| Calculate parking duration (ceiling rounding) | ✅ | `ParkingServiceImpl.calculateParkingFee()` |
| Calculate fee based on spot type and duration | ✅ | `ParkingServiceImpl.calculateParkingFee()` |
| Check for unpaid fines from previous parkings | ✅ | `FineDAO.findUnpaidByLicensePlate()` |
| Show: Hours parked, Parking fee, Unpaid fines, Total | ✅ | `EntryExitPanel` - summary labels |
| Accept payment (Cash/Card) | ✅ | `PaymentMethod.java`, `CardPaymentDialog.java` |
| Mark spot as available | ✅ | `ParkingSpot.releaseVehicle()` |
| Generate exit receipt | ✅ | `Receipt.java`, `ParkingServiceImpl.processExit()` |

### Feature 4: Reporting and Admin Views ✅
| Requirement | Status | Implementation |
|-------------|--------|----------------|
| View all floors and spots | ✅ | `AdminPanel.refreshOverviewTable()` |
| View occupancy rate | ✅ | `AdminPanel` - occupancyLabel, StatCards |
| View revenue (total fees collected) | ✅ | `ReportPanel.refreshRevenueReport()` |
| View vehicles currently parked | ✅ | `ReportPanel.refreshCurrentlyParkedVehicles()` |
| View unpaid fines | ✅ | `ReportPanel.refreshOutstandingFines()` |
| Choose fine scheme | ✅ | `AdminPanel.applyFineScheme()` |

## Fine Management Requirements

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| Fine if vehicle stays > 24 hours | ✅ | `ParkingServiceImpl.checkAndIssueFines()` |
| Fine if vehicle in Reserved without reservation | ✅ | `ParkingServiceImpl.checkAndIssueFines()` |
| Option A: Fixed Fine (RM50) | ✅ | `FixedFineStrategy.java` |
| Option B: Progressive Fine | ✅ | `ProgressiveFineStrategy.java` |
| Option C: Hourly Fine (RM20/hour) | ✅ | `HourlyFineStrategy.java` |
| Fines linked to license plate, not ticket | ✅ | `Fine.java` - has licensePlate field |
| Unpaid fines carry over to next exit | ✅ | `FineDAO.findUnpaidByLicensePlate()` |

## Payment Processing Requirements

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| Accept cash payment | ✅ | `PaymentMethod.CASH` |
| Accept card payment | ✅ | `PaymentMethod.CARD`, `CardPaymentDialog.java` |
| Receipt shows: Entry/Exit time | ✅ | `Receipt.java` - entryTime, exitTime |
| Receipt shows: Duration (hours) | ✅ | `Receipt.getDurationHours()` |
| Receipt shows: Parking fee breakdown | ✅ | `Receipt.getFeeBreakdown()` - "X hours × RMY = RMZ" |
| Receipt shows: Fines due | ✅ | `Receipt.getFineAmount()` |
| Receipt shows: Total amount paid | ✅ | `Receipt.getTotalAmount()` |
| Receipt shows: Payment method | ✅ | `Receipt.getPaymentMethod()` |
| Receipt shows: Remaining balance | ⚠️ | Not needed - exact payment assumed |

## UI Requirements

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| Admin Panel | ✅ | `AdminPanel.java` |
| Entry/Exit Panel | ✅ | `EntryExitPanel.java` |
| Reporting Panel | ✅ | `ReportPanel.java` |
| Java Swing | ✅ | All UI uses Swing |
| Professional appearance | ✅ | Custom styled components |
| Multiple panels/tabs | ✅ | `MainFrame.java` - sidebar navigation |
| Input validation with error messages | ✅ | `InputValidator.java`, JOptionPane errors |
| Button clicks trigger actions | ✅ | ActionListeners on all buttons |
| Display results clearly | ✅ | Tables, labels, dialogs |

## Design Pattern Requirements

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| Use one pattern from: Composite, Adapter, Bridge, Façade, Iterator, Observer, Builder, Prototype, Singleton | ✅ | Strategy Pattern (FineStrategy), Factory Pattern (VehicleFactory), Singleton (DatabaseManager), Composite (ParkingLot/Floor/Spot) |
| Pattern reflected in class diagram | ⚠️ | Need UML diagrams |
| Justify pattern selection | ⚠️ | Need documentation |

## Future-Proof Design (Interview Questions)

| Scenario | Status | How to Handle |
|----------|--------|---------------|
| Add new vehicle type (Bus) | ✅ | `Bus.java` created - extend `Vehicle`, add to `VehicleType` enum and `VehicleFactory` |
| Add new spot type (Electric charging) | ✅ | Add `ELECTRIC` to `SpotType` enum, create spots with RM8/hour rate |
| Add MaxCap fine scheme (max RM500) | ✅ | `MaxCapFineStrategy.java` already implemented |
| Add reservation system | ✅ | `Reservation.java`, `ReservationStatus.java`, `ReservationService.java` created |

## Missing/Incomplete Items

1. ~~**Spot ID Format**: Current format "F1-C01" doesn't include Row. PDF suggests "F1-R1-S1" (Floor-Row-Spot)~~ ✅ FIXED
2. ~~**Receipt Duration**: Receipt doesn't store parking duration in hours~~ ✅ FIXED
3. **Remaining Balance**: Not implemented (assumed exact payment)
4. **UML Diagrams**: Need Use Case, Class, and Sequence diagrams
5. ~~**5 Floors**: Currently only 3 floors, PDF suggests 5 floors~~ ✅ FIXED (now 5 floors)

## Code Quality Checklist

| Requirement | Status |
|-------------|--------|
| Code compiles without errors | ✅ |
| Code follows UML design | ⚠️ Need UML |
| Classes organized in packages | ✅ |
| Appropriate visibility (public/private) | ✅ |
| Comments for complex logic | ✅ |
| No compilation warnings | ✅ |
