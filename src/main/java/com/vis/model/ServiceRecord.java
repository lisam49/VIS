package com.vis.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

/**
 * Service record — implements Reportable so it can be displayed
 * polymorphically alongside other record types.
 */
public class ServiceRecord implements Reportable {

    private int serviceId;
    private int vehicleId;
    private String registrationNumber;
    private LocalDate serviceDate;
    private String serviceType;
    private String description;
    private double cost;

    public ServiceRecord() {}

    public ServiceRecord(int serviceId, int vehicleId, String registrationNumber,
                         LocalDate serviceDate, String serviceType,
                         String description, double cost) {
        this.serviceId = serviceId;
        this.vehicleId = vehicleId;
        this.registrationNumber = registrationNumber;
        this.serviceDate = serviceDate;
        this.serviceType = serviceType;
        this.description = description;
        this.cost = cost;
    }

    public int getServiceId()                  { return serviceId; }
    public void setServiceId(int serviceId)    { this.serviceId = serviceId; }
    public int getVehicleId()                  { return vehicleId; }
    public void setVehicleId(int vehicleId)    { this.vehicleId = vehicleId; }
    public String getRegistrationNumber()      { return registrationNumber; }
    public void setRegistrationNumber(String r){ this.registrationNumber = r; }
    public LocalDate getServiceDate()          { return serviceDate; }
    public void setServiceDate(LocalDate d)    { this.serviceDate = d; }
    public String getServiceType()             { return serviceType; }
    public void setServiceType(String t)       { this.serviceType = t; }
    public String getDescription()             { return description; }
    public void setDescription(String d)       { this.description = d; }
    public double getCost()                    { return cost; }
    public void setCost(double cost)           { this.cost = cost; }

    @Override
    public String getSummary() {
        return String.format("%s — %s ($%.2f) on %s",
                serviceType, description, cost, serviceDate);
    }

    @Override
    public String getCategory() {
        return "Workshop Service";
    }

    public SimpleIntegerProperty serviceIdProperty()           { return new SimpleIntegerProperty(serviceId); }
    public SimpleStringProperty registrationNumberProperty()   { return new SimpleStringProperty(registrationNumber); }
    public SimpleStringProperty serviceDateProperty()          { return new SimpleStringProperty(serviceDate == null ? "" : serviceDate.toString()); }
    public SimpleStringProperty serviceTypeProperty()          { return new SimpleStringProperty(serviceType); }
    public SimpleStringProperty descriptionProperty()          { return new SimpleStringProperty(description); }
    public SimpleDoubleProperty costProperty()                 { return new SimpleDoubleProperty(cost); }
}
