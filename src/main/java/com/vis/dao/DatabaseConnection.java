package com.vis.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public final class DatabaseConnection {

    private static String url;
    private static String user;
    private static String password;

    static {
        loadConfiguration();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println("PostgreSQL JDBC driver not found: " + ex.getMessage());
        }
    }

    private DatabaseConnection() {  }

    private static void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream in = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                props.load(in);
                url      = props.getProperty("db.url",      "jdbc:postgresql://localhost:5432/ lisebo_db");
                user     = props.getProperty("db.user",     "postgres");
                password = props.getProperty("db.password", "lisam");
                return;
            }
        } catch (IOException ex) {
            System.err.println("Could not read db.properties: " + ex.getMessage());
        }
        // defaults
        url      = "jdbc:postgresql://localhost:5432/lisebo_db";
        user     = "postgres";
        password = "lisam";
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static String getUrl() { return url; }
}
