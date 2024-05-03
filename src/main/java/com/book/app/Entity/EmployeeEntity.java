package com.book.app.Entity;

import java.time.LocalDate;

public class EmployeeEntity extends UserEntity {

    private String username;
    private String password;
    private Boolean admin;
    private Boolean enable;
    private LocalDate updatedAt;

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public Boolean getEnable() {
        return enable;
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

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
