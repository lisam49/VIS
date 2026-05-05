package com.vis.util;

import com.vis.model.AppUser;

public final class SessionManager {

    private static AppUser currentUser;

    private SessionManager() {}

    public static AppUser getCurrentUser()           { return currentUser; }
    public static void setCurrentUser(AppUser user)  { currentUser = user; }
    public static void clear()                       { currentUser = null; }
}
