package com.vis.dao;

import com.vis.model.ServiceRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceRecordDAO {

    public List<ServiceRecord> findAll() throws SQLException {
        List<ServiceRecord> list = new ArrayList<>();
        String sql = "SELECT service_id, registration_number, service_date, service_type, " +
                "       description, cost FROM vw_service_history";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                ServiceRecord r = new ServiceRecord();
                r.setServiceId(rs.getInt("service_id"));
                r.setRegistrationNumber(rs.getString("registration_number"));
                Date d = rs.getDate("service_date");
                if (d != null) r.setServiceDate(d.toLocalDate());
                r.setServiceType(rs.getString("service_type"));
                r.setDescription(rs.getString("description"));
                r.setCost(rs.getDouble("cost"));
                list.add(r);
            }
        }
        return list;
    }

    // FIXED: Use BigDecimal for NUMERIC type
    public void registerService(int vehicleId, LocalDate date,
                                String type, String description, double cost) throws SQLException {
        String sql = "CALL sp_register_service(?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             CallableStatement cs = c.prepareCall(sql)) {
            cs.setInt(1, vehicleId);
            cs.setDate(2, Date.valueOf(date));
            cs.setString(3, type);
            cs.setString(4, description);
            // Convert double to BigDecimal to match NUMERIC type
            cs.setBigDecimal(5, java.math.BigDecimal.valueOf(cost));
            cs.execute();
        }
    }
}