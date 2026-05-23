package com.example.library.vo;

import com.example.library.enums.UserRole;

public class LoginResponse {
    private String token;
    private UserRole role;
    private String username;

    public LoginResponse() {
    }

    public LoginResponse(String token, UserRole role, String username) {
        this.token = token;
        this.role = role;
        this.username = username;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
