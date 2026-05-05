# Vehicle Identification System (VIS)

A professional **JavaFX** desktop application built with the
**Model-View-Controller (MVC)** architectural pattern, backed by
**PostgreSQL** through **JDBC**.

The system helps police, workshops, insurance providers and customers
identify vehicles, track services, file reports, and manage violations.

---

## 1. Technology Stack

| Layer        | Technology                   |
|--------------|------------------------------|
| Frontend     | JavaFX 21                    |
| Backend      | PostgreSQL 14+               |
| DB driver    | JDBC (`org.postgresql:42.7`) |
| Build tool   | Maven                        |
| Language     | Java 17                      |
| IDE          | IntelliJ IDEA Community 2023+|
| Version ctrl | Git / GitHub                 |

---

## 2. Project Structure (MVC)

```
vehicle-identification-system/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/vis/
    │   ├── Main.java               ← JavaFX entry point
    │   ├── model/                  ← MODEL layer
    │   │   ├── Person.java         (abstract — INHERITANCE root)
    │   │   ├── Customer.java
    │   │   ├── Admin.java
    │   │   ├── Officer.java
    │   │   ├── Reportable.java     (interface — POLYMORPHISM)
    │   │   ├── Vehicle.java
    │   │   ├── ServiceRecord.java
    │   │   ├── PoliceReport.java
    │   │   ├── Violation.java
    │   │   └── AppUser.java
    │   ├── view/                   ← VIEW layer
    │   │   ├── LoginView.java
    │   │   ├── DashboardView.java
    │   │   ├── VehicleView.java
    │   │   ├── WorkshopView.java
    │   │   ├── CustomerView.java
    │   │   ├── PoliceView.java
    │   │   ├── InsuranceView.java
    │   │   └── BrowseVehiclesView.java
    │   ├── controller/             ← CONTROLLER layer
    │   │   ├── LoginController.java
    │   │   ├── VehicleController.java
    │   │   ├── WorkshopController.java
    │   │   ├── CustomerController.java
    │   │   ├── PoliceController.java
    │   │   └── InsuranceController.java
    │   ├── dao/                    ← Data Access Objects (JDBC)
    │   │   ├── DatabaseConnection.java
    │   │   ├── AppUserDAO.java
    │   │   ├── VehicleDAO.java
    │   │   ├── CustomerDAO.java
    │   │   ├── ServiceRecordDAO.java
    │   │   ├── PoliceReportDAO.java
    │   │   └── ViolationDAO.java
    │   └── util/
    │       ├── AlertUtil.java
    │       └── SessionManager.java
    └── resources/
        ├── db.properties           ← Edit DB credentials here
        └── database/schema.sql     ← Tables, views, procedures, seed data
```

---

## 3. Setup in IntelliJ IDEA Community

1. **Unzip** the project anywhere on disk.
2. Open **IntelliJ IDEA Community → File → Open** and select the
   `vehicle-identification-system` folder. IntelliJ detects the Maven
   project automatically and downloads all dependencies (JavaFX,
   PostgreSQL JDBC).
3. Make sure **Project SDK** is set to **JDK 17 or newer**
   (`File → Project Structure → Project`).
4. Wait for the Maven import to finish.

---

## 4. Database Setup (PostgreSQL)

1. Install PostgreSQL and create a database called `vis_db`:
   ```sql
   CREATE DATABASE vis_db;
   ```
2. Connect to it and run the schema script located at:
   ```
   src/main/resources/database/schema.sql
   ```
   This creates all tables, **views** (`vw_vehicle_full_details`,
   `vw_outstanding_violations`, `vw_service_history`), **stored
   procedures** (`sp_add_vehicle`, `sp_register_service`,
   `sp_pay_violation`) and seeds 25 vehicles, 10 customers and demo users.
3. Edit `src/main/resources/db.properties` with your local credentials:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/vis_db
   db.user=postgres
   db.password=YOUR_PASSWORD
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

## 6. Feature Map (matches the rubric)

| Requirement                                   | Where to find it                                              |
|-----------------------------------------------|---------------------------------------------------------------|
| **MVC architecture**                          | `model/`, `view/`, `controller/` packages                     |
| **Menu Bar & Menu Items**                     | `DashboardView.buildMenuBar()` (File > Refresh / Sign Out / Exit, View, Help) |
| **TableView**                                 | Every module view (Vehicle, Workshop, Customer, Police, Insurance) |
| **Pagination + ScrollPane**                   | `BrowseVehiclesView` — 25 vehicle cards, 5 per page           |
| **Progress Bar + Progress Indicator**         | Status bar in `DashboardView`, plus workshop progress         |
| **DropShadow visual effect**                  | Login button, info cards, browse cards                        |
| **FadeTransition**                            | Continuous fade on the login button & home cards              |
| **PostgreSQL via JDBC**                       | `dao/` package + `DatabaseConnection`                         |
| **Stored Procedures**                         | `sp_add_vehicle`, `sp_register_service`, `sp_pay_violation`   |
| **Views**                                     | `vw_vehicle_full_details`, `vw_outstanding_violations`, `vw_service_history` |
| **Inheritance**                               | `Person` → `Customer`, `Admin`, `Officer`                     |
| **Polymorphism**                              | `Reportable` implemented by `ServiceRecord`, `PoliceReport`, `Violation`; `Person.getRoleDescription()` overridden in subclasses |
| **Exception Handling**                        | try/catch around every JDBC call + UI alerts                  |
| **Repetition statements**                     | DAO `while (rs.next())` loops, page rendering loops           |
| **String Manipulation**                       | `trim()`, `toUpperCase()`, formatted summaries                |
| **File handling**                             | `db.properties` loaded via `DatabaseConnection.loadConfiguration()` |

---

## 7. GitHub

```bash
cd vehicle-identification-system
git init
git add .
git commit -m "Initial commit — Vehicle Identification System"
git branch -M main
git remote add origin https://github.com/<your-username>/vehicle-identification-system.git
git push -u origin main
```

Add the resulting repository link to your project documentation.

---

## 8. License

This project was created as a learning exercise. You are free to extend
and modify it for educational purposes.
