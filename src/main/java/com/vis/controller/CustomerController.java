package com.vis.controller;

import com.vis.dao.CustomerDAO;
import com.vis.model.Customer;

import java.sql.SQLException;
import java.util.List;

public class CustomerController {

    private final CustomerDAO dao = new CustomerDAO();

    public List<Customer> getAll() throws SQLException {
        return dao.findAll();
    }

    public void create(Customer c) throws SQLException {
        if (c.getName() == null || c.getName().isBlank())
            throw new IllegalArgumentException("Customer name is required");
        dao.create(c);
    }

    public void update(Customer c) throws SQLException {
        if (c.getId() <= 0)
            throw new IllegalArgumentException("Invalid customer ID");
        if (c.getName() == null || c.getName().isBlank())
            throw new IllegalArgumentException("Customer name is required");
        dao.update(c);
    }

    public void delete(int customerId) throws SQLException {
        if (customerId <= 0)
            throw new IllegalArgumentException("Invalid customer ID");
        dao.delete(customerId);
    }
}