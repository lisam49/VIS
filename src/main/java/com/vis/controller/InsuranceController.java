package com.vis.controller;

import com.vis.dao.InsuranceDAO;
import com.vis.model.Vehicle;

import java.sql.SQLException;
import java.util.List;

public class InsuranceController {

    private final InsuranceDAO dao = new InsuranceDAO();

    public List<Vehicle> getInsuredVehicles() throws SQLException {
        return dao.findAll();
    }

    public void registerForInsurance(String reg, String make, String model,
                                     int year, Integer ownerId) throws SQLException {
        dao.registerVehicle(reg.trim().toUpperCase(), make, model, year, ownerId);
    }

    public void updateInsuranceStatus(int vehicleId, String status) throws SQLException {
        dao.updateInsuranceStatus(vehicleId, status);
    }

    public void renewInsurancePolicy(int vehicleId) throws SQLException {
        dao.renewPolicy(vehicleId);
    }

    public List<Vehicle> getActivePolicies() throws SQLException {
        return dao.findActivePolicies();
    }

    public List<Vehicle> getInactivePolicies() throws SQLException {
        return dao.findInactivePolicies();
    }

    public List<Vehicle> getExpiringSoon() throws SQLException {
        return dao.findExpiringSoon();
    }

    public int getInsuredCount() throws SQLException {
        return dao.getInsuredCount();
    }

    public int getActiveCount() throws SQLException {
        return dao.getActiveCount();
    }
}