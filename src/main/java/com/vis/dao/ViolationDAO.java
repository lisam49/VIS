package com.vis.dao;

import com.vis.model.Violation;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ViolationDAO {

    public List<Violation> findAll() throws SQLException {
        List<Violation> list = new ArrayList<>();
        String sql = "SELECT vi.violation_id, vi.vehicle_id, v.registration_number, " +
                     "       vi.violation_date, vi.violation_type, vi.fine_amount, vi.status " +
                     "FROM Violation vi " +
                     "JOIN Vehicle v ON v.vehicle_id = vi.vehicle_id " +
                     "ORDER BY vi.violation_date DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    /** Uses the vw_outstanding_violations VIEW. */
    public List<Violation> findUnpaid() throws SQLException {
        List<Violation> list = new ArrayList<>();
        String sql = "SELECT v.violation_id, v.vehicle_id, ov.registration_number, " +
                     "       ov.violation_date, ov.violation_type, ov.fine_amount, ov.status " +
                     "FROM vw_outstanding_violations ov " +
                     "JOIN Violation v ON v.violation_id = ov.violation_id";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void create(int vehicleId, LocalDate date, String type,
                       double fine, String status) throws SQLException {
        String sql = "INSERT INTO Violation(vehicle_id, violation_date, violation_type, fine_amount, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, type);
            ps.setDouble(4, fine);
            ps.setString(5, status);
            ps.executeUpdate();
        }
    }

    /** Calls the sp_pay_violation PROCEDURE. */
    public void payViolation(int violationId) throws SQLException {
        String sql = "CALL sp_pay_violation(?)";
        try (Connection c = DatabaseConnection.getConnection();
             CallableStatement cs = c.prepareCall(sql)) {
            cs.setInt(1, violationId);
            cs.execute();
        }
    }

    private Violation map(ResultSet rs) throws SQLException {
        Violation v = new Violation();
        v.setViolationId(rs.getInt("violation_id"));
        v.setVehicleId(rs.getInt("vehicle_id"));
        v.setRegistrationNumber(rs.getString("registration_number"));
        Date d = rs.getDate("violation_date");
        if (d != null) v.setViolationDate(d.toLocalDate());
        v.setViolationType(rs.getString("violation_type"));
        v.setFineAmount(rs.getDouble("fine_amount"));
        v.setStatus(rs.getString("status"));
        return v;
    }
}
