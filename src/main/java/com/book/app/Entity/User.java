package com.book.app.Entity;

import java.sql.Date;
import java.time.LocalDate;

public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private LocalDate createdAt;
    private Boolean admin;
    private Boolean enable;

    public Boolean getAdmin() {
        return admin;
    }

    public Boolean getEnable() {
        return enable;
    }


    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }



    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
