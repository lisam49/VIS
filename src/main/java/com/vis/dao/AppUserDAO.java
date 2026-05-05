package com.vis.dao;

import com.vis.model.AppUser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppUserDAO {

    public Optional<AppUser> authenticate(String userId, String password) throws SQLException {
        String sql = "SELECT user_id, full_name, role, active FROM AppUser " +
                     "WHERE user_id = ? AND password = ? AND active = TRUE";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new AppUser(
                            rs.getString("user_id"),
                            rs.getString("full_name"),
                            AppUser.Role.valueOf(rs.getString("role")),
                            rs.getBoolean("active")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public List<AppUser> findAll() throws SQLException {
        List<AppUser> users = new ArrayList<>();
        String sql = "SELECT user_id, full_name, role, active FROM AppUser ORDER BY user_id";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new AppUser(
                        rs.getString("user_id"),
                        rs.getString("full_name"),
                        AppUser.Role.valueOf(rs.getString("role")),
                        rs.getBoolean("active")
                ));
            }
        }
        return users;
    }

    public void create(AppUser u, String password) throws SQLException {
        String sql = "INSERT INTO AppUser(user_id, password, role, full_name, active) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getUserId());
            ps.setString(2, password);
            ps.setString(3, u.getRole().name());
            ps.setString(4, u.getFullName());
            ps.setBoolean(5, u.isActive());
            ps.executeUpdate();
        }
    }

    public void setActive(String userId, boolean active) throws SQLException {
        String sql = "UPDATE AppUser SET active = ? WHERE user_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setString(2, userId);
            ps.executeUpdate();
        }
    }
}
