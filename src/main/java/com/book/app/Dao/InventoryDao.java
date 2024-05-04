package com.book.app.Dao;

import com.book.app.Entity.BookEntity;
import com.book.app.Entity.InventoryEntity;

import java.util.List;

public interface InventoryDao {
    List<InventoryEntity> getAllInventory(String keyword, String sort);
    boolean addInventory(List<BookEntity> bookEntityList);
    List<BookEntity> getBookToInventory(List<String> listBookSelected);
}
