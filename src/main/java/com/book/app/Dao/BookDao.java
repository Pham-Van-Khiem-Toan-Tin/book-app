package com.book.app.Dao;

import com.book.app.Entity.BookEntity;

import java.util.List;

public interface BookDao {
    List<BookEntity> getAllBook(String keyword, String sort);
    boolean addBook(BookEntity book);
    boolean editBook(BookEntity book);
    boolean lockOrUnLockBook(String id, Boolean enable);
    BookEntity getBookDetail(String bookId);
}
