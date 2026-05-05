package com.vis.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class Vehicle {

    private int vehicleId;
    private String registrationNumber;
    private String make;
    private String model;
    private int year;
    private int ownerId;
    private String ownerName;
    private String vin;
    private String insuranceStatus;
    private LocalDate insuranceExpiry;  // NEW: Track when insurance expires
     private String policyNumber;        // NEW: Unique policy number

    public Vehicle() {}

    public Vehicle(int vehicleId, String registrationNumber, String make,
                   String model, int year, int ownerId, String ownerName) {
        this.vehicleId = vehicleId;
        this.registrationNumber = registrationNumber;
        this.make = make;
        this.model = model;
        this.year = year;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.insuranceStatus = "Inactive";
        this.insuranceExpiry = null;
        this.policyNumber = null;
    }

    // Full constructor with VIN and insurance status
    public Vehicle(int vehicleId, String registrationNumber, String make,
                   String model, int year, int ownerId, String ownerName,
                   String vin, String insuranceStatus) {
        this.vehicleId = vehicleId;
        this.registrationNumber = registrationNumber;
        this.make = make;
        this.model = model;
        this.year = year;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.vin = vin;
        this.insuranceStatus = insuranceStatus;
        this.insuranceExpiry = null;
        this.policyNumber = null;
    }

    // Complete constructor with all fields
    public Vehicle(int vehicleId, String registrationNumber, String make,
                   String model, int year, int ownerId, String ownerName,
                   String vin, String insuranceStatus, LocalDate insuranceExpiry,
                   String policyNumber) {
        this.vehicleId = vehicleId;
        this.registrationNumber = registrationNumber;
        this.make = make;
        this.model = model;
        this.year = year;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.vin = vin;
        this.insuranceStatus = insuranceStatus;
        this.insuranceExpiry = insuranceExpiry;
        this.policyNumber = policyNumber;
    }

    // Getters and Setters
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }

    public String getInsuranceStatus() { return insuranceStatus; }
    public void setInsuranceStatus(String insuranceStatus) { this.insuranceStatus = insuranceStatus; }

    public LocalDate getInsuranceExpiry() { return insuranceExpiry; }
    public void setInsuranceExpiry(LocalDate insuranceExpiry) { this.insuranceExpiry = insuranceExpiry; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    // Helper method to check if policy is expired
    public boolean isExpired() {
        if (insuranceExpiry == null) return true;
        return LocalDate.now().isAfter(insuranceExpiry);
    }

    // Helper method to check if policy is active
    public boolean isActive() {
        return "Active".equals(insuranceStatus) && !isExpired();
    }

    // JavaFX Properties for TableView binding
    public SimpleIntegerProperty vehicleIdProperty() {
        return new SimpleIntegerProperty(vehicleId);
    }

    public SimpleStringProperty registrationNumberProperty() {
        return new SimpleStringProperty(registrationNumber);
    }

    public SimpleStringProperty makeProperty() {
        return new SimpleStringProperty(make);
    }

    public SimpleStringProperty modelProperty() {
        return new SimpleStringProperty(model);
    }

    public SimpleIntegerProperty yearProperty() {
        return new SimpleIntegerProperty(year);
    }

    public SimpleStringProperty ownerNameProperty() {
        return new SimpleStringProperty(ownerName);
    }

    public SimpleStringProperty vinProperty() {
        return new SimpleStringProperty(vin);
    }

    public SimpleStringProperty insuranceStatusProperty() {
        return new SimpleStringProperty(insuranceStatus);
    }

    public SimpleStringProperty insuranceExpiryProperty() {
        return new SimpleStringProperty(insuranceExpiry == null ? "N/A" : insuranceExpiry.toString());
    }

    public SimpleStringProperty policyNumberProperty() {
        return new SimpleStringProperty(policyNumber == null ? "Not Issued" : policyNumber);
    }

    @Override
    public String toString() {
        return registrationNumber + " - " + make + " " + model + " (" + year + ")";
    }
}