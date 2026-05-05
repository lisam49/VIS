package com.vis.dao;

import com.vis.model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsuranceDAO {

    /**
     * Get all insured vehicles (all vehicles with insurance information)
     */
    public List<Vehicle> findAll() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT vehicle_id, registration_number, make, model, year, " +
                "owner_id, insurance_status, insurance_expiry, policy_number " +
                "FROM Vehicle ORDER BY vehicle_id";

        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(mapVehicle(rs));
            }
        }
        return vehicles;
    }

    /**
     * Find a vehicle by ID
     */
    public Vehicle findById(int vehicleId) throws SQLException {
        String sql = "SELECT vehicle_id, registration_number, make, model, year, " +
                "owner_id, insurance_status, insurance_expiry, policy_number " +
                "FROM Vehicle WHERE vehicle_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapVehicle(rs);
                }
            }
        }
        return null;
    }

    /**
     * Find a vehicle by registration number
     */
    public Vehicle findByRegistration(String registration) throws SQLException {
        String sql = "SELECT vehicle_id, registration_number, make, model, year, " +
                "owner_id, insurance_status, insurance_expiry, policy_number " +
                "FROM Vehicle WHERE registration_number = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, registration);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapVehicle(rs);
                }
            }
        }
        return null;
    }

    /**
     * Register a vehicle for insurance (add to Vehicle table if not exists)
     */
    public void registerVehicle(String registration, String make, String model,
                                int year, Integer ownerId) throws SQLException {
        String sql = "INSERT INTO Vehicle(registration_number, make, model, year, owner_id, insurance_status) " +
                "VALUES (?, ?, ?, ?, ?, 'Inactive') " +
                "ON CONFLICT (registration_number) DO UPDATE SET " +
                "make = EXCLUDED.make, model = EXCLUDED.model, year = EXCLUDED.year, owner_id = EXCLUDED.owner_id";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, registration.toUpperCase());
            ps.setString(2, make);
            ps.setString(3, model);
            ps.setInt(4, year);
            if (ownerId == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, ownerId);
            }
            ps.executeUpdate();
        }
    }

    /**
     * Update insurance status of a vehicle
     */
    public void updateInsuranceStatus(int vehicleId, String status) throws SQLException {
        String sql = "UPDATE Vehicle SET insurance_status = ? WHERE vehicle_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, vehicleId);
            int rowsUpdated = ps.executeUpdate();
            System.out.println("Updated insurance status for vehicle ID " + vehicleId +
                    " to '" + status + "' - " + rowsUpdated + " row(s) affected");
        }
    }

    /**
     * Renew insurance policy (set new expiry date and ensure status is Active)
     */
    public void renewPolicy(int vehicleId) throws SQLException {
        String sql = "UPDATE Vehicle SET " +
                "insurance_status = 'Active', " +
                "insurance_expiry = CURRENT_DATE + INTERVAL '12 months', " +
                "policy_number = COALESCE(policy_number, 'POL-' || ? || '-' || EXTRACT(YEAR FROM CURRENT_DATE)) " +
                "WHERE vehicle_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.setInt(2, vehicleId);
            int rowsUpdated = ps.executeUpdate();
            System.out.println("Renewed policy for vehicle ID " + vehicleId + " - " + rowsUpdated + " row(s) affected");
        }
    }

    /**
     * Get all vehicles with active insurance
     */
    public List<Vehicle> findActivePolicies() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT vehicle_id, registration_number, make, model, year, " +
                "owner_id, insurance_status, insurance_expiry, policy_number " +
                "FROM Vehicle WHERE insurance_status = 'Active' ORDER BY vehicle_id";

        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(mapVehicle(rs));
            }
        }
        return vehicles;
    }

    /**
     * Get all vehicles with inactive insurance
     */
    public List<Vehicle> findInactivePolicies() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT vehicle_id, registration_number, make, model, year, " +
                "owner_id, insurance_status, insurance_expiry, policy_number " +
                "FROM Vehicle WHERE insurance_status = 'Inactive' ORDER BY vehicle_id";

        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(mapVehicle(rs));
            }
        }
        return vehicles;
    }

    /**
     * Get vehicles with expiring insurance (within next 30 days)
     */
    public List<Vehicle> findExpiringSoon() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT vehicle_id, registration_number, make, model, year, " +
                "owner_id, insurance_status, insurance_expiry, policy_number " +
                "FROM Vehicle WHERE insurance_status = 'Active' " +
                "AND insurance_expiry BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '30 days' " +
                "ORDER BY insurance_expiry";

        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(mapVehicle(rs));
            }
        }
        return vehicles;
    }

    /**
     * Delete a vehicle from insurance registry
     */
    public void deleteVehicle(int vehicleId) throws SQLException {
        String sql = "DELETE FROM Vehicle WHERE vehicle_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.executeUpdate();
            System.out.println("Deleted vehicle ID " + vehicleId + " from registry");
        }
    }

    /**
     * Generate a policy number for a vehicle
     */
    public String generatePolicyNumber(int vehicleId) throws SQLException {
        String sql = "UPDATE Vehicle SET policy_number = 'POL-' || ? || '-' || EXTRACT(YEAR FROM CURRENT_DATE) " +
                "WHERE vehicle_id = ? AND policy_number IS NULL RETURNING policy_number";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.setInt(2, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("policy_number");
                }
            }
        }
        return null;
    }

    /**
     * Map ResultSet to Vehicle object
     */
    private Vehicle mapVehicle(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setVehicleId(rs.getInt("vehicle_id"));
        v.setRegistrationNumber(rs.getString("registration_number"));
        v.setMake(rs.getString("make"));
        v.setModel(rs.getString("model"));
        v.setYear(rs.getInt("year"));
        v.setOwnerId(rs.getInt("owner_id"));

        // Get owner name separately (from Customer table)
        String ownerName = getOwnerName(v.getOwnerId());
        v.setOwnerName(ownerName);

        // Insurance fields
        v.setInsuranceStatus(rs.getString("insurance_status") != null ?
                rs.getString("insurance_status") : "Inactive");

        Date expiryDate = rs.getDate("insurance_expiry");
        if (expiryDate != null) {
            v.setInsuranceExpiry(expiryDate.toLocalDate());
        }

        v.setPolicyNumber(rs.getString("policy_number"));

        return v;
    }

    /**
     * Get owner name by owner ID
     */
    private String getOwnerName(int ownerId) throws SQLException {
        if (ownerId == 0) return "Unassigned";

        String sql = "SELECT name FROM Customer WHERE customer_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        }
        return "Unknown";
    }

    /**
     * Get count of insured vehicles
     */
    public int getInsuredCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Vehicle";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Get count of active policies
     */
    public int getActiveCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Vehicle WHERE insurance_status = 'Active'";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}