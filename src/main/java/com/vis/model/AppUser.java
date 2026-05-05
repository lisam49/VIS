package com.vis.model;

/**
 * Represents an application user (login record).
 */
public class AppUser {

    public enum Role { ADMIN, WORKSHOP, CUSTOMER, POLICE, INSURANCE }

    private String userId;
    private String fullName;
    private Role role;
    private boolean active;

    public AppUser() {}

    public AppUser(String userId, String fullName, Role role, boolean active) {
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
        this.active = active;
    }

    public String getUserId()              { return userId; }
    public void setUserId(String userId)   { this.userId = userId; }
    public String getFullName()            { return fullName; }
    public void setFullName(String name)   { this.fullName = name; }
    public Role getRole()                  { return role; }
    public void setRole(Role role)         { this.role = role; }
    public boolean isActive()              { return active; }
    public void setActive(boolean active)  { this.active = active; }
}
