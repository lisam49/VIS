package com.vis.controller;

import com.vis.dao.ServiceRecordDAO;
import com.vis.model.ServiceRecord;

import java.sql.SQLException;
import java.util.List;

public class ServiceRecordController {

    private final ServiceRecordDAO dao = new ServiceRecordDAO();

    public List<ServiceRecord> getAll() throws SQLException {
        return dao.findAll();
    }
}