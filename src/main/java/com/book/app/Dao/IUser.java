package com.book.app.Dao;

import com.book.app.Entity.User;

import java.util.List;

public interface IUser {
    List<User> getAllUser();
    boolean addUser(User user);
    boolean editUser(User user);
    boolean lockOrUnLockUser(int id, Boolean enable);
}
