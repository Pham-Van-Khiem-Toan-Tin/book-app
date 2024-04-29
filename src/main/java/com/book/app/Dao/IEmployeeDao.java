package com.book.app.Dao;

import com.book.app.Entity.EmployeeEntity;

import java.util.List;

public interface IEmployeeDao {
    EmployeeEntity login(String username, String password);
    List<EmployeeEntity> getAllEmployee();
    boolean addEmployee(EmployeeEntity employee);
    boolean editEmployee(EmployeeEntity employee);
    boolean lockOrUnLockEmployee(int id, Boolean enable);
    boolean resetPassword(int id, String newPassword);
}
