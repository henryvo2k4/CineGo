package com.example.cinego;

public class User {
    private String fullName;
    private String email;
    private String phone;
    private String dob; // Ngày sinh (Day/Month/Year)
    private String role; // "user" hoặc "admin"

    public User() {
        // Hàm tạo rỗng bắt buộc cho Firebase
    }

    public User(String fullName, String email, String phone, String dob, String role) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.role = role;
    }

    // Các hàm Getter (Lấy dữ liệu ra)
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDob() { return dob; }
    public String getRole() { return role; }
}