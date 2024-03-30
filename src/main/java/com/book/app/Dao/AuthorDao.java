package com.book.app.Dao;


import com.book.app.Entity.AuthorEntity;

import java.util.List;

public interface AuthorDao {
    List<AuthorEntity> getAllAuthor(String keyword, String sort);
    boolean addAuthor(AuthorEntity employee);
    boolean editAuthor(AuthorEntity employee);
    boolean lockOrUnLockAuthor(String id, Boolean enable);
}
