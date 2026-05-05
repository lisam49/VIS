package com.vis.model;

/**
 * Polymorphic contract implemented by ServiceRecord, PoliceReport,
 * and Violation. Allows uniform formatting of records throughout the UI.
 */
public interface Reportable {
    String getSummary();
    String getCategory();
}
