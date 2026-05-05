package com.vis.model;

/**
 * Abstract base class for all people in the system.
 * D
 *  emonstrates INHERITANCE — Customer, Officer, Admin all extend Person.
 * Demonstrates POLYMORPHISM via the abstract getRoleDescription() method.
 */
public abstract class Person {

    protected int id;
    protected String name;
    protected String phone;
    protected String email;

    protected Person(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public int getId()           { return id; }
    public String getName()      { return name; }
    public String getPhone()     { return phone; }
    public String getEmail()     { return email; }

    public void setId(int id)               { this.id = id; }
    public void setName(String name)        { this.name = name; }
    public void setPhone(String phone)      { this.phone = phone; }
    public void setEmail(String email)      { this.email = email; }

    public abstract String getRoleDescription();

    @Override
    public String toString() {
        return getRoleDescription() + ": " + name;
    }
}
