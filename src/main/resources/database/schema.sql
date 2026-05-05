DROP VIEW IF EXISTS vw_outstanding_violations CASCADE;
DROP VIEW IF EXISTS vw_service_history CASCADE;
DROP VIEW IF EXISTS vw_vehicle_full_details CASCADE;

DROP TABLE IF EXISTS Violation CASCADE;
DROP TABLE IF EXISTS PoliceReport CASCADE;
DROP TABLE IF EXISTS CustomerQuery CASCADE;
DROP TABLE IF EXISTS ServiceRecord CASCADE;
DROP TABLE IF EXISTS Vehicle CASCADE;
DROP TABLE IF EXISTS Customer CASCADE;
DROP TABLE IF EXISTS AppUser CASCADE;

CREATE TABLE AppUser (
    user_id      VARCHAR(50)  PRIMARY KEY,
    password     VARCHAR(100) NOT NULL,
    role         VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN','WORKSHOP','CUSTOMER','POLICE','INSURANCE')),
    full_name    VARCHAR(100) NOT NULL,
    active       BOOLEAN      DEFAULT TRUE,
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Customer (
    customer_id  SERIAL       PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    address      VARCHAR(255),
    phone        VARCHAR(20),
    email        VARCHAR(100)
);

CREATE TABLE Vehicle (
    vehicle_id           SERIAL       PRIMARY KEY,
    registration_number  VARCHAR(20)  UNIQUE NOT NULL,
    make                 VARCHAR(50),
    model                VARCHAR(50),
    year                 INTEGER,
    owner_id             INTEGER REFERENCES Customer(customer_id) ON DELETE SET NULL
);

CREATE TABLE ServiceRecord (
    service_id    SERIAL       PRIMARY KEY,
    vehicle_id    INTEGER REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE,
    service_date  DATE         NOT NULL,
    service_type  VARCHAR(50),
    description   TEXT,
    cost          NUMERIC(10,2)
);

CREATE TABLE CustomerQuery (
    query_id      SERIAL       PRIMARY KEY,
    customer_id   INTEGER REFERENCES Customer(customer_id) ON DELETE CASCADE,
    vehicle_id    INTEGER REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE,
    query_date    DATE         NOT NULL,
    query_text    TEXT,
    response_text TEXT
);

CREATE TABLE PoliceReport (
    report_id     SERIAL       PRIMARY KEY,
    vehicle_id    INTEGER REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE,
    report_date   DATE         NOT NULL,
    report_type   VARCHAR(50),
    description   TEXT,
    officer_name  VARCHAR(100)
);

CREATE TABLE Violation (
    violation_id    SERIAL       PRIMARY KEY,
    vehicle_id      INTEGER REFERENCES Vehicle(vehicle_id) ON DELETE CASCADE,
    violation_date  DATE         NOT NULL,
    violation_type  VARCHAR(50),
    fine_amount     NUMERIC(10,2),
    status          VARCHAR(10) CHECK (status IN ('Paid','Unpaid'))
);

ALTER TABLE Vehicle
ADD COLUMN IF NOT EXISTS insurance_status VARCHAR(20) DEFAULT 'Inactive';

ALTER TABLE Vehicle
ADD COLUMN IF NOT EXISTS insurance_expiry DATE;

ALTER TABLE Vehicle
ADD COLUMN IF NOT EXISTS policy_number VARCHAR(50);

CREATE OR REPLACE VIEW vw_vehicle_full_details AS
SELECT
    v.vehicle_id,
    v.registration_number,
    v.make,
    v.model,
    v.year,
    c.customer_id,
    c.name        AS owner_name,
    c.phone       AS owner_phone,
    c.email       AS owner_email,
    v.insurance_status,
    v.insurance_expiry,
    v.policy_number
FROM Vehicle v
LEFT JOIN Customer c ON v.owner_id = c.customer_id;

CREATE OR REPLACE VIEW vw_outstanding_violations AS
SELECT
    v.violation_id,
    ve.registration_number,
    v.violation_type,
    v.violation_date,
    v.fine_amount,
    v.status
FROM Violation v
JOIN Vehicle ve ON ve.vehicle_id = v.vehicle_id
WHERE v.status = 'Unpaid';

CREATE OR REPLACE VIEW vw_service_history AS
SELECT
    s.service_id,
    ve.registration_number,
    s.service_date,
    s.service_type,
    s.description,
    s.cost
FROM ServiceRecord s
JOIN Vehicle ve ON ve.vehicle_id = s.vehicle_id
ORDER BY s.service_date DESC;

CREATE OR REPLACE PROCEDURE sp_add_vehicle(
    p_registration  VARCHAR,
    p_make          VARCHAR,
    p_model         VARCHAR,
    p_year          INTEGER,
    p_owner_id      INTEGER
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO Vehicle(registration_number, make, model, year, owner_id)
    VALUES (p_registration, p_make, p_model, p_year, p_owner_id);
END;
$$;

CREATE OR REPLACE PROCEDURE sp_register_service(
    p_vehicle_id    INTEGER,
    p_service_date  DATE,
    p_service_type  VARCHAR,
    p_description   TEXT,
    p_cost          NUMERIC
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO ServiceRecord(vehicle_id, service_date, service_type, description, cost)
    VALUES (p_vehicle_id, p_service_date, p_service_type, p_description, p_cost);
END;
$$;

CREATE OR REPLACE PROCEDURE sp_pay_violation(p_violation_id INTEGER)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE Violation SET status = 'Paid' WHERE violation_id = p_violation_id;
END;
$$;

UPDATE Vehicle
SET
    insurance_expiry = CURRENT_DATE + INTERVAL '12 months',
    policy_number = 'POL-' || vehicle_id || '-' || EXTRACT(YEAR FROM CURRENT_DATE),
    insurance_status = 'Active'
WHERE insurance_status = 'Inactive' OR insurance_status IS NULL;

CREATE INDEX IF NOT EXISTS idx_vehicle_insurance_status ON Vehicle(insurance_status);
CREATE INDEX IF NOT EXISTS idx_vehicle_insurance_expiry ON Vehicle(insurance_expiry);
CREATE INDEX IF NOT EXISTS idx_vehicle_registration ON Vehicle(registration_number);
CREATE INDEX IF NOT EXISTS idx_service_vehicle ON ServiceRecord(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_service_date ON ServiceRecord(service_date);
CREATE INDEX IF NOT EXISTS idx_violation_vehicle ON Violation(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_police_vehicle ON PoliceReport(vehicle_id);

INSERT INTO AppUser(user_id, password, role, full_name) VALUES
('admin', 'admin123', 'ADMIN', 'System Administrator'),
('workshop1', 'workshop123', 'WORKSHOP', 'Maseru Auto Workshop'),
('customer1', 'customer123', 'CUSTOMER', 'Relebohile Mosoatsi'),
('police1', 'police123', 'POLICE', 'Officer Thabo Mokoena'),
('insurance1', 'insurance123', 'INSURANCE', 'SafeDrive Insurance');

INSERT INTO Customer(name, address, phone, email) VALUES
('Relebohile Mosoatsi', '12 Kingsway Road, Maseru', '555-0101', 'relebohile.mosoatsi@gmail.com'),
('Lethusang Morobi', '45 Cathedral Avenue, Maseru', '555-0102', 'lethusang.morobi@gmail.com'),
('Relebohile Lebitsa', '78 Pioneer Road, Maseru', '555-0103', 'relebohile.lebitsa@gmail.com'),
('Makhula Matlali', '23 Moshoeshoe Street, Mafeteng', '555-0104', 'makhula.matlali@gmail.com'),
('Sekhoane Sello', '90 Main North Road, Leribe', '555-0105', 'sekhoane.sello@gmail.com'),
('Reitumetse Nkalai', '34 Hospital Road, Berea', '555-0106', 'reitumetse.nkalai@gmail.com'),
('Keneuoe Maribe', '67 Industrial Area, Maseru', '555-0107', 'keneuoe.maribe@gmail.com'),
('Nthati Sekoati', '89 Borokhoaneng, Maseru', '555-0108', 'nthati.sekoati@gmail.com'),
('Lerato Mohapi', '11 Lithoteng Drive, Maseru', '555-0109', 'lerato.mohapi@gmail.com'),
('Thabiso Harries', '22 Mohalalitoe Road, Mohale''s Hoek', '555-0110', 'thabiso.harries@gmail.com'),
('Lisebo Mokhutoane', '15 Moshoeshoe Road, Maseru', '555-0111', 'lisebo.mokhutoane@gmail.com'),
('Matlotlo Mahloane', '28 Independence Avenue, Maseru', '555-0112', 'matlotlo.mahloane@gmail.com'),
('Thato Mohlanka', '33 Parliament Road, Maseru', '555-0113', 'thato.mohlanka@gmail.com'),
('Malika Nrou', '42 Airport Junction, Maseru', '555-0114', 'malika.nrou@gmail.com'),
('Selimo Mphatse', '55 Hillside View, Maseru', '555-0115', 'selimo.mphatse@gmail.com'),
('Rethabile Ntsupa', '68 Lower Thetsane, Maseru', '555-0116', 'rethabile.ntsupa@gmail.com');

INSERT INTO Vehicle(registration_number, make, model, year, owner_id) VALUES
('ABC-1001', 'Toyota', 'Corolla', 2020, 1),
('ABC-1002', 'Honda', 'Civic', 2019, 2),
('ABC-1003', 'Ford', 'Focus', 2021, 3),
('ABC-1004', 'Hyundai', 'Elantra', 2018, 4),
('ABC-1005', 'Nissan', 'Sentra', 2022, 5),
('ABC-1006', 'Chevrolet', 'Malibu', 2020, 6),
('ABC-1007', 'Mazda', 'Mazda3', 2019, 7),
('ABC-1008', 'Volkswagen', 'Jetta', 2021, 8),
('ABC-1009', 'Kia', 'Forte', 2022, 9),
('ABC-1010', 'Subaru', 'Impreza', 2020, 10),
('ABC-1011', 'Toyota', 'Camry', 2023, 11),
('ABC-1012', 'Honda', 'Accord', 2022, 12),
('ABC-1013', 'BMW', '3 Series', 2021, 13),
('ABC-1014', 'Mercedes', 'C-Class', 2020, 14),
('ABC-1015', 'Audi', 'A4', 2019, 15),
('ABC-1016', 'Lexus', 'IS', 2021, 16),
('ABC-1017', 'Toyota', 'Highlander', 2022, 1),
('ABC-1018', 'Honda', 'CR-V', 2021, 2),
('ABC-1019', 'Ford', 'Escape', 2020, 3),
('ABC-1020', 'Jeep', 'Cherokee', 2019, 4),
('ABC-1021', 'Mazda', 'CX-5', 2022, 5),
('ABC-1022', 'Toyota', 'Rav4', 2023, 6),
('ABC-1023', 'Nissan', 'Qashqai', 2022, 7),
('ABC-1024', 'Hyundai', 'Tucson', 2021, 8),
('ABC-1025', 'Kia', 'Sportage', 2022, 9);

INSERT INTO ServiceRecord(vehicle_id, service_date, service_type, description, cost) VALUES
(1, '2025-01-15', 'Oil Change', 'Standard oil and filter replacement', 750.00),
(2, '2025-02-10', 'Brake Service', 'Front brake pad replacement', 2205.00),
(3, '2025-03-05', 'Tire Rotation', 'Rotate and balance all tires', 600.00),
(4, '2025-03-20', 'Battery', 'Replaced 12V battery', 1800.00),
(5, '2025-04-01', 'Inspection', 'Annual safety inspection', 450.00),
(6, '2025-04-12', 'Transmission', 'Transmission fluid flush', 2100.00),
(1, '2025-04-18', 'Tune Up', 'Spark plugs and air filter', 1550.00);

INSERT INTO PoliceReport(vehicle_id, report_date, report_type, description, officer_name) VALUES
(2, '2025-02-22', 'Accident', 'Minor rear-end collision at Main & 5th', 'Thabo Mokoena'),
(7, '2025-03-11', 'Theft', 'Vehicle reported stolen, recovered', 'Nthati Sekoati'),
(13, '2025-04-02', 'Accident', 'Side-swipe in parking lot', 'Keneuoe Maribe');

INSERT INTO Violation(vehicle_id, violation_date, violation_type, fine_amount, status) VALUES
(1, '2025-01-08', 'Speeding', 1500.00, 'Paid'),
(3, '2025-02-15', 'Parking', 600.00, 'Unpaid'),
(5, '2025-03-22', 'Red Light', 2000.00, 'Unpaid'),
(8, '2025-04-05', 'No Insurance', 5000.00, 'Unpaid'),
(11, '2025-04-15', 'Speeding', 1800.00, 'Paid');

INSERT INTO CustomerQuery(customer_id, vehicle_id, query_date, query_text, response_text) VALUES
(1, 1, '2025-04-01', 'When is my next service due?', 'Recommended after 5,000 km or 6 months.'),
(2, 2, '2025-04-05', 'Brake squeal after replacement.', 'Please book a follow-up inspection.');

SELECT * FROM vw_vehicle_full_details LIMIT 5;

SELECT customer_id, name, email FROM Customer;

SELECT
    (SELECT COUNT(*) FROM Customer) AS Customers,
    (SELECT COUNT(*) FROM Vehicle) AS Vehicles,
    (SELECT COUNT(*) FROM AppUser) AS Users;

SELECT vehicle_id, registration_number, insurance_status, insurance_expiry, policy_number
FROM Vehicle
LIMIT 5;