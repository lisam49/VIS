package com.vis.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class Violation implements Reportable {

    private int violationId;
    private int vehicleId;
    private String registrationNumber;
    private LocalDate violationDate;
    private String violationType;
    private double fineAmount;
    private String status; // Paid / Unpaid

    public Violation() {}

    public Violation(int violationId, int vehicleId, String registrationNumber,
                     LocalDate violationDate, String violationType,
                     double fineAmount, String status) {
        this.violationId = violationId;
        this.vehicleId = vehicleId;
        this.registrationNumber = registrationNumber;
        this.violationDate = violationDate;
        this.violationType = violationType;
        this.fineAmount = fineAmount;
        this.status = status;
    }

    public int getViolationId()                { return violationId; }
    public void setViolationId(int id)         { this.violationId = id; }
    public int getVehicleId()                  { return vehicleId; }
    public void setVehicleId(int v)            { this.vehicleId = v; }
    public String getRegistrationNumber()      { return registrationNumber; }
    public void setRegistrationNumber(String r){ this.registrationNumber = r; }
    public LocalDate getViolationDate()        { return violationDate; }
    public void setViolationDate(LocalDate d)  { this.violationDate = d; }
    public String getViolationType()           { return violationType; }
    public void setViolationType(String t)     { this.violationType = t; }
    public double getFineAmount()              { return fineAmount; }
    public void setFineAmount(double f)        { this.fineAmount = f; }
    public String getStatus()                  { return status; }
    public void setStatus(String s)            { this.status = s; }

    @Override
    public String getSummary() {
        return String.format("%s on %s — fine $%.2f (%s)",
                violationType, violationDate, fineAmount, status);
    }

    @Override
    public String getCategory() {
        return "Traffic Violation";
    }

    public SimpleIntegerProperty violationIdProperty()         { return new SimpleIntegerProperty(violationId); }
    public SimpleStringProperty registrationNumberProperty()   { return new SimpleStringProperty(registrationNumber); }
    public SimpleStringProperty violationDateProperty()        { return new SimpleStringProperty(violationDate == null ? "" : violationDate.toString()); }
    public SimpleStringProperty violationTypeProperty()        { return new SimpleStringProperty(violationType); }
    public SimpleDoubleProperty fineAmountProperty()           { return new SimpleDoubleProperty(fineAmount); }
    public SimpleStringProperty statusProperty()               { return new SimpleStringProperty(status); }
}
