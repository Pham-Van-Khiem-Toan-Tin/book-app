package com.book.app.Dao;

import com.book.app.Entity.BookEntity;
import com.book.app.Entity.CustomerEntity;
import com.book.app.Entity.OrderEntity;

import java.util.List;

public interface OrderDao {
    List<OrderEntity> getAllOrder(String keyword, String sort);
    boolean addOrder(List<BookEntity> bookEntityList, CustomerEntity customer);
    List<BookEntity> getBookToOrder(List<String> listBookSelected);
}
