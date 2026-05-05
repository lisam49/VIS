package com.vis.controller;

import com.vis.dao.VehicleDAO;
import com.vis.model.Vehicle;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class VehicleController {

    private final VehicleDAO dao = new VehicleDAO();

    public List<Vehicle> getAll() throws SQLException             { return dao.findAll(); }
    public Optional<Vehicle> findByReg(String reg) throws SQLException { return dao.findByRegistration(reg); }

    public void addVehicle(String reg, String make, String model,
                           int year, Integer ownerId) throws SQLException {
        if (reg == null || reg.isBlank()) throw new IllegalArgumentException("Registration is required");
        if (year < 1900 || year > 2100)   throw new IllegalArgumentException("Year is out of range");
        dao.addVehicle(reg.trim().toUpperCase(), make, model, year, ownerId);
    }

    public void delete(int vehicleId) throws SQLException { dao.delete(vehicleId); }
}
