package com.vis.controller;

import com.vis.dao.ServiceRecordDAO;
import com.vis.model.ServiceRecord;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class WorkshopController {

    private final ServiceRecordDAO dao = new ServiceRecordDAO();

    public List<ServiceRecord> getAll() throws SQLException {
        return dao.findAll();
    }

    public void registerService(int vehicleId, LocalDate date,
                                String type, String description, double cost) throws SQLException {
        if (date == null) throw new IllegalArgumentException("Service date is required");
        if (type == null || type.isBlank()) throw new IllegalArgumentException("Service type is required");
        if (cost < 0) throw new IllegalArgumentException("Cost cannot be negative");
        dao.registerService(vehicleId, date, type, description, cost);
    }

    /**
     * Get the number of services completed today
     * Used for the progress bar in WorkshopView
     */
    public int getTodayServiceCount() throws SQLException {
        List<ServiceRecord> all = dao.findAll();
        LocalDate today = LocalDate.now();
        int count = 0;
        for (ServiceRecord record : all) {
            if (record.getServiceDate() != null && record.getServiceDate().equals(today)) {
                count++;
            }
        }
        return count;
    }
}