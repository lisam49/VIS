package com.vis.dao;

import com.vis.model.PoliceReport;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PoliceReportDAO {

    public List<PoliceReport> findAll() throws SQLException {
        List<PoliceReport> list = new ArrayList<>();
        String sql = "SELECT pr.report_id, pr.vehicle_id, v.registration_number, " +
                     "       pr.report_date, pr.report_type, pr.description, pr.officer_name " +
                     "FROM PoliceReport pr " +
                     "JOIN Vehicle v ON v.vehicle_id = pr.vehicle_id " +
                     "ORDER BY pr.report_date DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                PoliceReport r = new PoliceReport();
                r.setReportId(rs.getInt("report_id"));
                r.setVehicleId(rs.getInt("vehicle_id"));
                r.setRegistrationNumber(rs.getString("registration_number"));
                Date d = rs.getDate("report_date");
                if (d != null) r.setReportDate(d.toLocalDate());
                r.setReportType(rs.getString("report_type"));
                r.setDescription(rs.getString("description"));
                r.setOfficerName(rs.getString("officer_name"));
                list.add(r);
            }
        }
        return list;
    }

    public void create(int vehicleId, LocalDate date, String type,
                       String description, String officerName) throws SQLException {
        String sql = "INSERT INTO PoliceReport(vehicle_id, report_date, report_type, description, officer_name) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, type);
            ps.setString(4, description);
            ps.setString(5, officerName);
            ps.executeUpdate();
        }
    }
}
