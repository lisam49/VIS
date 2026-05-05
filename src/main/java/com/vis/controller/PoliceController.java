package com.vis.controller;

import com.vis.dao.PoliceReportDAO;
import com.vis.dao.ViolationDAO;
import com.vis.model.PoliceReport;
import com.vis.model.Violation;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PoliceController {

    private final PoliceReportDAO reportDAO = new PoliceReportDAO();
    private final ViolationDAO violationDAO = new ViolationDAO();

    public List<PoliceReport> getReports() throws SQLException     { return reportDAO.findAll(); }
    public List<Violation>    getViolations() throws SQLException  { return violationDAO.findAll(); }
    public List<Violation>    getUnpaid() throws SQLException      { return violationDAO.findUnpaid(); }

    public void fileReport(int vehicleId, LocalDate date, String type,
                           String description, String officer) throws SQLException {
        reportDAO.create(vehicleId, date, type, description, officer);
    }

    public void issueViolation(int vehicleId, LocalDate date, String type,
                               double fine, String status) throws SQLException {
        violationDAO.create(vehicleId, date, type, fine, status);
    }

    public void payViolation(int violationId) throws SQLException {
        violationDAO.payViolation(violationId);
    }
}
