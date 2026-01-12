# Interview Preparation Guide

## Design Patterns Implemented

### 1. Strategy Pattern (Primary Pattern for Fine Calculation)

**Location:** `com.university.parking.domain.FineStrategy`

```
FineStrategy (interface)
├── FixedFineStrategy      - Flat RM50 fine
├── ProgressiveFineStrategy - Tiered: RM50/100/150/200
└── HourlyFineStrategy     - RM20 per hour
```

**Why Strategy Pattern?**
- Allows runtime switching of fine calculation algorithms
- Admin can change fine scheme without modifying existing code
- Easy to add new fine schemes (e.g., MaxCapFineStrategy)

**Code Example - Adding New Fine Scheme:**
```java
// Add Maximum Cap Fine Strategy (fine cannot exceed RM500)
public class MaxCapFineStrategy implements FineStrategy {
    private final FineStrategy baseStrategy;
    private final double maxCap;
    
    public MaxCapFineStrategy(FineStrategy baseStrategy, double maxCap) {
        this.baseStrategy = baseStrategy;
        this.maxCap = maxCap;
    }
    
    @Override
    public double calculateFine(int overstayHours) {
        double fine = baseStrategy.calculateFine(overstayHours);
        return Math.min(fine, maxCap);
    }
    
    @Override
    public double getMaxCap() {
        return maxCap;
    }
}

// Usage:
fineService.setFineStrategy(new MaxCapFineStrategy(new HourlyFineStrategy(), 500.0));
```

### 2. Factory Pattern (Vehicle Creation)

**Location:** `com.university.parking.domain.VehicleFactory`

**Why Factory Pattern?**
- Centralizes vehicle creation logic
- Easy to add new vehicle types
- Encapsulates the decision of which class to instantiate

**Code Example - Adding Bus Vehicle Type:**
```java
// Step 1: Add to VehicleType enum
public enum VehicleType {
    MOTORCYCLE, CAR, SUV_TRUCK, HANDICAPPED, BUS  // Add BUS
}

// Step 2: Create Bus class
public class Bus extends Vehicle {
    public Bus(String licensePlate) {
        super(licensePlate);
    }
    
    @Override
    public List<SpotType> getAllowedSpotTypes() {
        return Arrays.asList(SpotType.REGULAR, SpotType.RESERVED);
    }
    
    @Override
    public VehicleType getVehicleType() {
        return VehicleType.BUS;
    }
}

// Step 3: Update VehicleFactory
case BUS:
    return new Bus(licensePlate);
```

### 3. Composite Pattern (Parking Structure)

**Location:** `com.university.parking.domain.ParkingLot`, `Floor`, `ParkingSpot`

```
ParkingLot (Composite)
└── Floor[] (Composite)
    └── ParkingSpot[] (Leaf)
```

**Why Composite Pattern?**
- Treats individual spots and groups (floors, lot) uniformly
- Easy to calculate statistics at any level
- Supports hierarchical structure naturally

### 4. Singleton Pattern (Database Manager)

**Location:** `com.university.parking.db.DatabaseManager`

**Why Singleton?**
- Ensures single database connection
- Provides global access point
- Controls resource usage

---

## Future-Proof Design Analysis

### a) Add New Vehicle Type (Bus)

**Changes Required:** 3 files, no refactoring needed

1. Add `BUS` to `VehicleType` enum
2. Create `Bus.java` extending `Vehicle`
3. Add case in `VehicleFactory`

### b) Add New Parking Spot Type (Electric Vehicle Charging)

**Changes Required:** 2 files

1. Add `ELECTRIC` to `SpotType` enum
2. Update `ParkingSpot.canAccommodate()` if needed
3. Create spots in database with RM8/hour rate

```java
// In SpotType.java
public enum SpotType {
    COMPACT, REGULAR, HANDICAPPED, RESERVED, ELECTRIC
}

// In Application.java - create sample data
createSpot("F1-E01", SpotType.ELECTRIC, 8.0);
```

### c) Add New Fine Scheme (Maximum Cap)

**Changes Required:** 1 new file only

```java
public class MaxCapFineStrategy implements FineStrategy {
    // Decorator pattern - wraps any strategy with a cap
    private final FineStrategy baseStrategy;
    private final double maxCap;
    
    @Override
    public double calculateFine(int hours) {
        return Math.min(baseStrategy.calculateFine(hours), maxCap);
    }
}
```

### d) Add Reservation System

**Changes Required:** New module, minimal changes to existing code

```java
// New domain class
public class Reservation {
    private String reservationId;
    private String licensePlate;
    private String spotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;
}

// New DAO
public interface ReservationDAO extends GenericDAO<Reservation, String> {
    List<Reservation> findBySpotId(String spotId);
    List<Reservation> findByLicensePlate(String licensePlate);
}

// Update ParkingSpot - add reservation check
public boolean isAvailableForReservation(LocalDateTime start, LocalDateTime end) {
    // Check against existing reservations
}
```

---

## OOP Principles Explanation

### Encapsulation

**Examples in Code:**

1. **ParkingSpot** - Private fields with controlled access
```java
public class ParkingSpot {
    private final String spotId;        // Immutable
    private SpotStatus status;          // Controlled via methods
    private String currentVehiclePlate; // Only set via assignVehicle()
    
    public void assignVehicle(String plate) {
        if (!isAvailable()) {
            throw new IllegalStateException("Spot is occupied");
        }
        this.currentVehiclePlate = plate;
        this.status = SpotStatus.OCCUPIED;
    }
}
```

2. **Vehicle** - Protected constructor, abstract methods
```java
public abstract class Vehicle {
    private final String licensePlate;  // Cannot be changed after creation
    
    protected Vehicle(String licensePlate) {
        // Validation in constructor
    }
}
```

3. **Service Layer** - Business logic hidden from UI
```java
// UI only calls service methods, doesn't know implementation details
parkingService.processEntry(plate, type, spotId);
```

### Inheritance

**Examples in Code:**

1. **Vehicle Hierarchy**
```
Vehicle (abstract)
├── Motorcycle
├── Car
├── SUVTruck
└── HandicappedVehicle
```

2. **Benefits:**
- Common fields (licensePlate, entryTime, exitTime) in parent
- Each subclass defines its own `getAllowedSpotTypes()`
- `HandicappedVehicle` has special pricing logic

### Polymorphism

**Examples in Code:**

1. **Strategy Pattern** - Runtime behavior change
```java
FineStrategy strategy = new ProgressiveFineStrategy();
double fine = strategy.calculateFine(hours);  // Polymorphic call

// Later, admin changes strategy
strategy = new HourlyFineStrategy();
fine = strategy.calculateFine(hours);  // Different behavior, same interface
```

2. **Vehicle Type Handling**
```java
Vehicle vehicle = VehicleFactory.createVehicle(type, plate);
List<SpotType> allowed = vehicle.getAllowedSpotTypes();  // Polymorphic
```

3. **DAO Pattern**
```java
// Code works with interface, not implementation
ParkingSpotDAO dao = new ParkingSpotDAOImpl(dbManager);
dao.save(spot);  // Polymorphic - could be different DB implementation
```

---

## Design Pattern Benefits

### What Would Be Difficult Without Strategy Pattern?

```java
// WITHOUT Strategy Pattern - Hard to maintain
public double calculateFine(int hours, String schemeType) {
    if (schemeType.equals("FIXED")) {
        return 50.0;
    } else if (schemeType.equals("PROGRESSIVE")) {
        // Complex tiered calculation
    } else if (schemeType.equals("HOURLY")) {
        return hours * 20.0;
    }
    // Adding new scheme requires modifying this method
    // Violates Open/Closed Principle
}
```

### With Strategy Pattern - Easy to Extend

```java
// WITH Strategy Pattern - Clean and extensible
public interface FineStrategy {
    double calculateFine(int overstayHours);
}

// Adding new scheme = new class, no existing code changes
public class NewScheme implements FineStrategy {
    @Override
    public double calculateFine(int hours) {
        // New calculation logic
    }
}
```

### How Pattern Makes Adding Features Easier

| Feature | Without Pattern | With Pattern |
|---------|-----------------|--------------|
| New fine scheme | Modify existing if-else | Add new class |
| New vehicle type | Modify multiple places | Add class + factory case |
| Change algorithm | Risk breaking existing | Swap strategy object |
| Testing | Hard to isolate | Easy to mock |

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                           │
│  MainFrame, EntryExitPanel, AdminPanel, ReportPanel    │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                   Service Layer                         │
│  ParkingService, FineService, PaymentService, Report   │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                    Domain Layer                         │
│  Vehicle, ParkingSpot, Ticket, Fine, Payment, Receipt  │
│  FineStrategy (Strategy Pattern)                        │
│  VehicleFactory (Factory Pattern)                       │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                     DAO Layer                           │
│  ParkingSpotDAO, VehicleDAO, TicketDAO, FineDAO        │
└─────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│                   Database Layer                        │
│  DatabaseManager (Singleton), SQLite                    │
└─────────────────────────────────────────────────────────┘
```

---

## Key Interview Points

1. **Strategy Pattern** is the PRIMARY design pattern - used for fine calculation
2. **Factory Pattern** for vehicle creation - supports Open/Closed Principle
3. **Composite Pattern** for parking structure - ParkingLot > Floor > Spot
4. **Singleton Pattern** for database connection management
5. **DAO Pattern** for data persistence abstraction
6. **Service Layer** separates business logic from UI

## Sample Interview Q&A

**Q: Why did you choose Strategy Pattern?**

A: The requirement states admin should be able to choose ANY fine scheme. Strategy Pattern allows:
- Runtime switching without code changes
- Easy addition of new schemes (just implement interface)
- Each strategy is independently testable
- Follows Open/Closed Principle

**Q: How would you add a Bus vehicle type?**

A: Three simple steps:
1. Add `BUS` to `VehicleType` enum
2. Create `Bus` class extending `Vehicle` with appropriate `getAllowedSpotTypes()`
3. Add case in `VehicleFactory.createVehicle()`

No existing code needs modification - demonstrates Open/Closed Principle.

**Q: How does your design support future enhancements?**

A: Through:
- **Interfaces** - Can swap implementations (DAO, Strategy)
- **Inheritance** - Add new vehicle/spot types easily
- **Layered Architecture** - Changes in one layer don't affect others
- **Factory Pattern** - Centralized object creation
- **Strategy Pattern** - Pluggable algorithms
