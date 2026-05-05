package com.vis.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class PoliceReport implements Reportable {

    private int reportId;
    private int vehicleId;
    private String registrationNumber;
    private LocalDate reportDate;
    private String reportType;
    private String description;
    private String officerName;

    public PoliceReport() {}

    public PoliceReport(int reportId, int vehicleId, String registrationNumber,
                        LocalDate reportDate, String reportType,
                        String description, String officerName) {
        this.reportId = reportId;
        this.vehicleId = vehicleId;
        this.registrationNumber = registrationNumber;
        this.reportDate = reportDate;
        this.reportType = reportType;
        this.description = description;
        this.officerName = officerName;
    }

    public int getReportId()                { return reportId; }
    public void setReportId(int id)         { this.reportId = id; }
    public int getVehicleId()               { return vehicleId; }
    public void setVehicleId(int v)         { this.vehicleId = v; }
    public String getRegistrationNumber()   { return registrationNumber; }
    public void setRegistrationNumber(String r) { this.registrationNumber = r; }
    public LocalDate getReportDate()        { return reportDate; }
    public void setReportDate(LocalDate d)  { this.reportDate = d; }
    public String getReportType()           { return reportType; }
    public void setReportType(String t)     { this.reportType = t; }
    public String getDescription()          { return description; }
    public void setDescription(String d)    { this.description = d; }
    public String getOfficerName()          { return officerName; }
    public void setOfficerName(String n)    { this.officerName = n; }

    @Override
    public String getSummary() {
        return String.format("[%s] %s — %s (filed by %s)",
                reportType, reportDate, description, officerName);
    }

    @Override
    public String getCategory() {
        return "Police Report";
    }

    public SimpleIntegerProperty reportIdProperty()             { return new SimpleIntegerProperty(reportId); }
    public SimpleStringProperty registrationNumberProperty()    { return new SimpleStringProperty(registrationNumber); }
    public SimpleStringProperty reportDateProperty()            { return new SimpleStringProperty(reportDate == null ? "" : reportDate.toString()); }
    public SimpleStringProperty reportTypeProperty()            { return new SimpleStringProperty(reportType); }
    public SimpleStringProperty descriptionProperty()           { return new SimpleStringProperty(description); }
    public SimpleStringProperty officerNameProperty()           { return new SimpleStringProperty(officerName); }
}
