package com.vis.model;

/**
 * Police officer — files reports and violations. Inherits from Person.
 */
public class Officer extends Person {

    private String badgeNumber;

    public Officer(int id, String name, String phone, String email, String badgeNumber) {
        super(id, name, phone, email);
        this.badgeNumber = badgeNumber;
    }

    public String getBadgeNumber()                     { return badgeNumber; }
    public void setBadgeNumber(String badgeNumber)     { this.badgeNumber = badgeNumber; }

    @Override
    public String getRoleDescription() {
        return "Police Officer";
    }
}
