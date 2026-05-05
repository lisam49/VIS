package com.vis.controller;

import com.vis.dao.AppUserDAO;
import com.vis.model.AppUser;
import com.vis.util.SessionManager;

import java.sql.SQLException;
import java.util.Optional;

public class LoginController {

    private final AppUserDAO userDAO = new AppUserDAO();

    public Optional<AppUser> login(String userId, String password) throws SQLException {
        if (userId == null || userId.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("User ID and password are required");
        }
        Optional<AppUser> user = userDAO.authenticate(userId.trim(), password);
        user.ifPresent(SessionManager::setCurrentUser);
        return user;
    }
}
