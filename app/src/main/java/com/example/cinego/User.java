package com.example.cinego;

public class User {
    private String fullName;
    private String email;
    private String phone;
    private String dob;
    private String role;

    public User() {
    } // Constructor trống cho Firebase

    public User(String fullName, String email, String phone, String dob, String role) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.role = role;
    }

    // --- THÊM CÁC HÀM GETTER NÀY VÀO ---
    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDob() {
        return dob;
    }

    public String getRole() {
        return role;
    }
}