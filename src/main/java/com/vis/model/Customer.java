package com.vis.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Customer — owner of one or more vehicles. Inherits from Person.
 */
public class Customer extends Person {

    private String address;

    public Customer() {
        super(0, "", "", "");
    }

    public Customer(int id, String name, String address, String phone, String email) {
        super(id, name, phone, email);
        this.address = address;
    }

    public String getAddress()              { return address; }
    public void setAddress(String address)  { this.address = address; }

    @Override
    public String getRoleDescription() {
        return "Customer";
    }

    // JavaFX TableView properties
    public SimpleIntegerProperty idProperty()       { return new SimpleIntegerProperty(id); }
    public SimpleStringProperty nameProperty()      { return new SimpleStringProperty(name); }
    public SimpleStringProperty addressProperty()   { return new SimpleStringProperty(address); }
    public SimpleStringProperty phoneProperty()     { return new SimpleStringProperty(phone); }
    public SimpleStringProperty emailProperty()     { return new SimpleStringProperty(email); }
}
