package com.book.app.Dao;

import com.book.app.Entity.AuthorEntity;
import com.book.app.Entity.CategoryEntity;

import java.util.List;

public interface CategoryDao {
    List<CategoryEntity> getAllCategory(String keyword, String sort);
    boolean addCategory(CategoryEntity category);
    boolean editCategory(CategoryEntity category);
    boolean lockOrUnLockCategory(String id, Boolean enable);
    List<CategoryEntity> getAllCategoryName();
    List<CategoryEntity> getAllCategoryOfBook(String bookId);
}
