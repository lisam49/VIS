# Vehicle Identification System (VIS)

A professional **JavaFX** desktop application built with the
**Model-View-Controller (MVC)** architectural pattern, backed by
**PostgreSQL** through **JDBC**.

The system helps police, workshops, insurance providers and customers
identify vehicles, track services, file reports, and manage violations.

---

## 1. Technology Stack

| Layer | Technology |
|-------|------------|
| Frontend | JavaFX 19+ |
| Backend | PostgreSQL 14+ |
| Database Driver | JDBC (PostgreSQL 42.5.1) |
| Build Tool | Maven |
| Language | Java 17+ |
| IDE | IntelliJ IDEA |
| Version Control | Git / GitHub |

---
---

## 2. Project Structure (MVC)

vehicle-identification-system/
├── pom.xml
├── README.md
├── CONTRIBUTION.md
└── src/main/
├── java/com/vis/
│   ├── Main.java
│   ├── model/
│   │   ├── BaseModel.java
│   │   ├── Person.java
│   │   ├── Customer.java
│   │   ├── Admin.java
│   │   ├── Officer.java
│   │   ├── Reportable.java
│   │   ├── Vehicle.java
│   │   ├── ServiceRecord.java
│   │   ├── PoliceReport.java
│   │   ├── Violation.java
│   │   ├── AppUser.java
│   │   └── CustomerQuery.java
│   ├── view/
│   │   ├── BaseView.java
│   │   ├── LoginView.java
│   │   ├── DashboardView.java
│   │   ├── VehicleView.java
│   │   ├── WorkshopView.java
│   │   ├── CustomerView.java
│   │   ├── PoliceView.java
│   │   ├── InsuranceView.java
│   │   ├── BrowseVehiclesView.java
│   │   ├── CustomerVehicleView.java
│   │   ├── CustomerBrowseVehicleView.java
│   │   ├── SystemRecordsView.java
│   │   └── UserManagementView.java
│   ├── controller/
│   │   ├── LoginController.java
│   │   ├── VehicleController.java
│   │   ├── WorkshopController.java
│   │   ├── CustomerController.java
│   │   ├── PoliceController.java
│   │   └── InsuranceController.java
│   ├── dao/
│   │   ├── DatabaseConnection.java
│   │   ├── AppUserDAO.java
│   │   ├── VehicleDAO.java
│   │   ├── CustomerDAO.java
│   │   ├── ServiceRecordDAO.java
│   │   ├── PoliceReportDAO.java
│   │   ├── ViolationDAO.java
│   │   └── InsuranceDAO.java
│   └── util/
│       ├── AlertUtil.java
│       └── SessionManager.java
└── resources/
├── db.properties
└── database/
└── schema.sql
---

## 3. Setup in IntelliJ IDEA Community

## Setup Instructions

### Prerequisites
- Java 17 or newer installed
- PostgreSQL 14 or newer installed
- IntelliJ IDEA (or any Java IDE)

### Step 1: Open Project in IntelliJ

1. Open IntelliJ IDEA
2. Click **File → Open**
3. Select the `vehicle-identification-system` folder
4. IntelliJ will automatically detect the Maven project and download dependencies (JavaFX, PostgreSQL JDBC)

### Step 2: Configure JDK

1. Go to **File → Project Structure → Project**
2. Set **Project SDK** to **JDK 17** or newer
3. Click **OK**

### Step 3: Wait for Maven Import

- Let IntelliJ finish downloading all dependencies
- Check the progress bar at the bottom of the IDE

### Step 4: Set Up the Database

1. Create a PostgreSQL database:
```sql
CREATE DATABASE lisebo_db;

## 4. Database Setup (PostgreSQL)

1. Install PostgreSQL and create a database called `lisebo_db`:
   ```sql
   CREATE DATABASE lisebo_db;
   ```
2. Connect to it and run the schema script located at:

This creates:
- **8 tables:** AppUser, Customer, Vehicle, ServiceRecord, PoliceReport, Violation, CustomerQuery
- **3 views:** `vw_vehicle_full_details`, `vw_service_history`, `vw_outstanding_violations`
- **3 stored procedures:** `sp_add_vehicle`, `sp_register_service`, `sp_pay_violation`
- **Sample data:** 5 users, 16 customers, 25 vehicles, 7 service records, 
- 3 police reports, 5 violations
3. Edit `src/main/resources/db.properties` with your local credentials:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/lisebo_db
   db.user=postgres
   db.password=lisam
   ```

---

## 5. Running

In IntelliJ:

* Open `src/main/java/com/vis/Main.java`
* Click the green ▶ icon next to the `main` method.

Or from the command line:

```bash
mvn clean javafx:run
```

### Demo accounts

| User ID     | Password      | Role      |
|-------------|---------------|-----------|
| admin       | admin123      | Admin     |
| workshop1   | workshop123   | Workshop  |
| customer1   | customer123   | Customer  |
| police1     | police123     | Police    |
| insurance1  | insurance123  | Insurance |

---

## 6. Feature Map 
| Requirement | Where to find it |
|-------------|------------------|
| **MVC architecture** | `model/`, `view/`, `controller/` packages |
| **Menu Bar & Menu Items** | `DashboardView.buildStyledMenuBar()` - File menu (Refresh, Sign Out, Exit), View menu (Full Screen), Help menu (About) |
| **TableView** | `CustomerView.java`, `VehicleView.java`, `PoliceView.java`, `InsuranceView.java`, `WorkshopView.java` |
| **Pagination** | `BrowseVehiclesView.java` - 6 vehicles per page with page controls |
| **ScrollPane (20+ items)** | `SystemRecordsView.java` - 25 dummy records arranged vertically |
| **Progress Bar & Progress Indicator** | `WorkshopView.java` - Today's workshop progress bar; `DashboardView.java` - Status bar with progress indicator |
| **DropShadow visual effect** | `LoginView.java` - Sign In button; `DashboardView.java` - Stat cards and quick action buttons; `BrowseVehiclesView.java` - Vehicle cards |
| **FadeTransition** | `LoginView.java` - Continuous fade on Sign In button; `DashboardView.java` - Fade animation when swapping views |
| **PostgreSQL via JDBC** | `dao/DatabaseConnection.java` + all DAO classes (`VehicleDAO.java`, `CustomerDAO.java`, `AppUserDAO.java`, etc.) |
| **Stored Procedures** | `database/schema.sql` - `sp_add_vehicle`, `sp_register_service`, `sp_pay_violation` |
| **Database Views** | `database/schema.sql` - `vw_vehicle_full_details`, `vw_service_history`, `vw_outstanding_violations` |
| **Inheritance** | `model/Person.java` (abstract) → `Customer.java`, `Admin.java`, `Officer.java`; `model/BaseModel.java` (abstract) → `Vehicle.java`, `Customer.java`, `AppUser.java` |
| **Polymorphism** | `model/Reportable.java` interface implemented by `ServiceRecord.java`, `PoliceReport.java`, `Violation.java`; `Person.getRoleDescription()` overridden in subclasses; `BaseModel.getDisplayName()` overridden in subclasses |
| **Exception Handling** | `try-catch` blocks in all controllers (`LoginController.java`, `VehicleController.java`, etc.) and DAOs; `AlertUtil.java` for user-friendly error messages |
| **Repetition statements** | DAO `while(rs.next())` loops for database results; `for` loops in `BrowseVehiclesView.java` for pagination; `for` loops in `SystemRecordsView.java` for 25 items |
| **String Manipulation** | `trim()` and `toUpperCase()` on registration numbers; `String.format()` for currency display; `getSummary()` methods concatenating strings |
| **File handling** | `db.properties` loaded via `DatabaseConnection.loadConfiguration()` using `Properties` class and `InputStream` |
| **Role-Based Access Control** | `DashboardView.java` - `switch(user.getRole())` shows different menu items for ADMIN, WORKSHOP, CUSTOMER, POLICE, INSURANCE |
| **Second-Hand Vehicle Marketplace** | `CustomerBrowseVehicleView.java` - Browse vehicles and contact owners via dialog |
| **Insurance Policy Management** | `InsuranceView.java` - Register, Activate, and Renew policies with status color-coding |

## 7. GitHub

```bash
cd vehicle-identification-system
git init
git add .
git commit -m "Initial commit — Vehicle Identification System"
git branch -M main
git remote add origin https://github.com/lisam49/VIS.git
git push -u origin main
```

