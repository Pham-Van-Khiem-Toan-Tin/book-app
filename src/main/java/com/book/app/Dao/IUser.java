package com.book.app.Dao;

import com.book.app.Entity.User;

import java.util.List;

public interface IUser {
    User login(String username, String password);
    List<User> getAllUser();
    boolean addUser(User user);
    boolean editUser(User user);
    boolean lockOrUnLockUser(int id, Boolean enable);
    boolean resetPassword(int id, String newPassword);
}
