package com.clinic.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String phone;
    private String nric;
    private String role;

    public User(String username, String password, String fullName, String phone, String nric, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.nric = nric;
        this.role = role;
    }

    public User(int id, String username, String password, String fullName, String phone, String nric, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.nric = nric;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNric() { return nric; }
    public void setNric(String nric) { this.nric = nric; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}