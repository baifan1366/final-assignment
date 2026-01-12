# Requirements Document

## Introduction

本系统是一个大学停车场管理系统（University Parking Lot Management System），采用 Java Swing 构建的本地桌面应用程序，使用 SQLite 作为嵌入式数据库。系统支持车辆入场/出场管理、停车费计算、罚款管理、支付处理及管理报表功能。

## Glossary

- **ParkingLot**: 停车场整体，管理所有楼层和全局查询
- **Floor**: 停车场楼层，维护该层所有停车位
- **ParkingSpot**: 停车位，系统最小业务单元，包含类型和状态
- **Vehicle**: 车辆抽象类，包含车牌号和入场/出场时间
- **Ticket**: 停车凭证，入场时生成的值对象
- **Fine**: 罚款记录，绑定车牌号，独立于 Ticket
- **FineStrategy**: 罚款计算策略接口
- **Payment**: 支付记录，包含金额、方式和时间
- **Receipt**: 出场收据，包含停车费和罚款明细
- **DAO**: 数据访问对象，负责数据库操作
- **Service**: 业务服务层，封装业务逻辑

## Requirements

### Requirement 1: Parking Structure Management

**User Story:** As a system administrator, I want to manage the parking lot structure with multiple floors and spots, so that I can organize parking resources effectively.

#### Acceptance Criteria

1. THE ParkingLot SHALL contain one or more Floor objects
2. THE Floor SHALL contain one or more ParkingSpot objects
3. WHEN a ParkingSpot is created, THE System SHALL assign a unique spotId, type, hourlyRate, and initial status of Available
4. THE ParkingSpot type SHALL be one of: Compact, Regular, Handicapped, or Reserved
5. WHEN querying available spots, THE ParkingLot SHALL return spots filtered by vehicle type compatibility
6. THE ParkingLot SHALL calculate and provide global statistics including total spots, available spots, and occupancy rate

### Requirement 2: Vehicle Management

**User Story:** As a parking attendant, I want to register different types of vehicles, so that I can apply appropriate parking rules based on vehicle type.

#### Acceptance Criteria

1. THE System SHALL support the following vehicle types: Motorcycle, Car, SUVTruck, and HandicappedVehicle
2. WHEN a Vehicle is created, THE System SHALL record its licensePlate
3. THE Vehicle SHALL define getAllowedSpotTypes() to return compatible spot types
4. WHEN a HandicappedVehicle parks, THE System SHALL allow it to park in any spot type
5. WHEN a HandicappedVehicle parks in a Handicapped spot, THE System SHALL apply zero hourly rate
6. WHEN a HandicappedVehicle parks in a non-Handicapped spot, THE System SHALL apply RM2/hour rate

### Requirement 3: Vehicle Entry Flow

**User Story:** As a parking attendant, I want to process vehicle entry efficiently, so that vehicles can be assigned to appropriate parking spots.

#### Acceptance Criteria

1. WHEN a vehicle enters, THE System SHALL accept licensePlate and vehicleType as input
2. WHEN processing entry, THE System SHALL query available spots based on vehicle type compatibility
3. WHEN displaying available spots, THE System SHALL show spot details in a table format
4. WHEN a user selects a spot, THE System SHALL validate the spot is available and compatible
5. WHEN validation passes, THE System SHALL bind the Vehicle to the ParkingSpot and record entryTime
6. WHEN entry is complete, THE System SHALL generate a Ticket with format T-{PLATE}-{TIMESTAMP}
7. IF licensePlate is empty or vehicleType is not selected, THEN THE System SHALL display an error message
8. IF no spot is selected, THEN THE System SHALL prevent entry completion

### Requirement 4: Vehicle Exit Flow

**User Story:** As a parking attendant, I want to process vehicle exit with accurate fee calculation, so that customers can pay and leave efficiently.

#### Acceptance Criteria

1. WHEN processing exit, THE System SHALL accept licensePlate as input
2. WHEN a vehicle is found, THE System SHALL calculate parking duration using ceiling method (round up to next hour)
3. WHEN calculating parking fee, THE System SHALL multiply duration by spot's hourlyRate
4. WHEN processing exit, THE System SHALL query all unpaid fines for the licensePlate
5. WHEN displaying exit summary, THE System SHALL show parking hours, parking fee, and unpaid fines separately
6. WHEN payment is complete, THE System SHALL release the ParkingSpot and set its status to Available
7. WHEN exit is complete, THE System SHALL generate a Receipt with all payment details
8. IF licensePlate is not found in parked vehicles, THEN THE System SHALL display an error message

### Requirement 5: Fine Management

**User Story:** As a system administrator, I want to configure different fine calculation schemes, so that I can apply appropriate penalty policies.

#### Acceptance Criteria

1. THE System SHALL support multiple fine strategies: FixedFineStrategy, ProgressiveFineStrategy, and HourlyFineStrategy
2. WHEN admin selects a fine scheme, THE System SHALL apply it to all future parking sessions
3. THE Fine SHALL be associated with licensePlate, independent of Ticket
4. THE Fine SHALL persist across multiple entry/exit sessions until paid
5. WHEN calculating fine, THE System SHALL apply a maximum cap if configured
6. WHEN querying fines, THE FineManager SHALL aggregate all unpaid fines for a given licensePlate

### Requirement 6: Payment Processing

**User Story:** As a parking attendant, I want to process payments with multiple methods, so that customers can pay conveniently.

#### Acceptance Criteria

1. THE System SHALL support payment methods: Cash and Card
2. WHEN processing payment, THE System SHALL record amount, method, and timestamp
3. WHEN payment is complete, THE System SHALL generate a Receipt
4. THE Receipt SHALL include parking fee, fine amount, total amount, and payment method
5. WHEN payment fails or is cancelled, THE System SHALL not release the ParkingSpot

### Requirement 7: Admin Panel and Reporting

**User Story:** As a system administrator, I want to view parking lot status and generate reports, so that I can monitor operations and make decisions.

#### Acceptance Criteria

1. WHEN viewing parking lot overview, THE AdminPanel SHALL display all floors and spots with their status
2. WHEN viewing statistics, THE AdminPanel SHALL show current occupancy percentage
3. WHEN viewing revenue report, THE System SHALL aggregate all parking fees and paid fines from database
4. WHEN viewing outstanding fines, THE System SHALL list all unpaid fines with licensePlate and amount
5. WHEN viewing currently parked vehicles, THE System SHALL list all vehicles with entry time and spot information
6. THE AdminPanel SHALL allow fine scheme selection without directly modifying ParkingSpot status

### Requirement 8: User Interface Design

**User Story:** As a user, I want a clear and professional interface, so that I can operate the system efficiently.

#### Acceptance Criteria

1. THE MainFrame SHALL use BorderLayout with NORTH for title and CENTER for main content
2. THE System SHALL use JTabbedPane for navigation between Entry/Exit, Admin, and Reports panels
3. THE EntryExitPanel SHALL be divided into Vehicle Entry (left) and Vehicle Exit (right) sections
4. WHEN displaying spot lists or reports, THE System SHALL use JTable for data presentation
5. THE System SHALL use JComboBox for vehicle type and payment method selection
6. THE System SHALL use JRadioButton with ButtonGroup for fine scheme selection
7. WHEN input validation fails, THE System SHALL display error using JOptionPane
8. THE UI SHALL use light background colors and dark text for readability
9. THE UI layer SHALL only handle user interaction; all business logic SHALL be in Service layer

### Requirement 9: Data Persistence

**User Story:** As a system administrator, I want data to be persisted reliably, so that information is not lost between sessions.

#### Acceptance Criteria

1. THE System SHALL use SQLite as the embedded database
2. THE System SHALL implement DAO pattern for all database operations
3. THE Database SHALL contain tables for: parking_spot, vehicle, ticket, fine, and payment
4. WHEN the application starts, THE System SHALL initialize database connection and create tables if not exist
5. WHEN any business operation completes, THE System SHALL persist changes to database immediately
