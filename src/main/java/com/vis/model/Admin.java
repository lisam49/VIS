package com.vis.model;

/**
 * Admin user — manages access for other users. Inherits from Person.
 */
public class Admin extends Person {

    public Admin(int id, String name, String phone, String email) {
        super(id, name, phone, email);
    }

    @Override
    public String getRoleDescription() {
        return "Administrator";
    }
}
