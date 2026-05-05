package com.vis.dao;

import com.vis.model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleDAO {

    /** Uses the vw_vehicle_full_details VIEW. */
    public List<Vehicle> findAll() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT vehicle_id, registration_number, make, model, year, " +
                     "       customer_id, owner_name FROM vw_vehicle_full_details " +
                     "ORDER BY vehicle_id";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(map(rs));
            }
        }
        return vehicles;
    }

    public Optional<Vehicle> findByRegistration(String registration) throws SQLException {
        String sql = "SELECT vehicle_id, registration_number, make, model, year, " +
                     "       customer_id, owner_name FROM vw_vehicle_full_details " +
                     "WHERE registration_number = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, registration);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        }
        return Optional.empty();
    }

    /** Calls the sp_add_vehicle PROCEDURE. */
    public void addVehicle(String registration, String make, String model,
                           int year, Integer ownerId) throws SQLException {
        String sql = "CALL sp_add_vehicle(?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             CallableStatement cs = c.prepareCall(sql)) {
            cs.setString(1, registration);
            cs.setString(2, make);
            cs.setString(3, model);
            cs.setInt(4, year);
            if (ownerId == null) cs.setNull(5, Types.INTEGER);
            else                 cs.setInt(5, ownerId);
            cs.execute();
        }
    }

    public void delete(int vehicleId) throws SQLException {
        String sql = "DELETE FROM Vehicle WHERE vehicle_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.executeUpdate();
        }
    }

    private Vehicle map(ResultSet rs) throws SQLException {
        return new Vehicle(
                rs.getInt("vehicle_id"),
                rs.getString("registration_number"),
                rs.getString("make"),
                rs.getString("model"),
                rs.getInt("year"),
                rs.getInt("customer_id"),
                rs.getString("owner_name")
        );
    }
}
