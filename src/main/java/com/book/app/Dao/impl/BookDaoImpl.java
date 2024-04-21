package com.book.app.Dao.impl;

import com.book.app.Config.DBConnection;
import com.book.app.Dao.BookDao;
import com.book.app.Entity.AuthorEntity;
import com.book.app.Entity.BookEntity;
import com.book.app.Entity.CategoryEntity;
import com.book.app.Entity.PublisherEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {
    private DBConnection db = new DBConnection();
    private ResultSet resultSet;
    @Override
    public List<BookEntity> getAllBook(String keyword, String sort) {
        List<BookEntity> bookEntityList = new ArrayList<>();
        String sql = "SELECT b.book_id, b.title, b.public_id_image, b.url_image, b.quantity," +
                " b.description, b.publisherId, b.isEnable, b.created_at," +
                " CASE WHEN isb.price" +
                " IS NULL THEN NULL ELSE isb.price END AS price" +
                " FROM book b" +
                " LEFT JOIN (" +
                " SELECT book_id, price" +
                " FROM imported_books ib" +
                " INNER JOIN book_imported_sheet bis ON ib.imported_sheet_id = bis.imported_sheet_id" +
                " WHERE bis.created_at = ( SELECT min( created_at ) FROM book_imported_sheet" +
                " WHERE created_at < now() AND stoke > 0 ))" +
                " AS isb ON b.book_id = isb.book_id";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql = sql + " WHERE  title LIKE '%" + keyword + "%' OR price LIKE '%" + keyword + "%' OR description LIKE '%" + keyword + "%' OR (CAST(created_at AS CHAR) LIKE '%" + keyword + "%')";
        }
        if (sort != null) {

        }
        System.out.println(sql);
        try {
            db.initPrepar(sql);
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                BookEntity book = new BookEntity();
                book.setId(resultSet.getString("book_id"));
                book.setImage_url(resultSet.getString("url_image"));
                book.setImage_public_id(resultSet.getString("public_id_image"));
                book.setName(resultSet.getString("title"));
                book.setDescription(resultSet.getString("description"));
                int isEnableValue = resultSet.getInt("isEnable");
                Boolean isEnable = (isEnableValue == 1);
                book.setEnable(isEnable);
                book.setCreated_at(resultSet.getTimestamp("created_at").toLocalDateTime());
                book.setQuantity(resultSet.getInt("quantity"));
                book.setPrice(resultSet.getDouble("price"));
                bookEntityList.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return bookEntityList;
    }

    @Override
    public boolean addBook(BookEntity book) {
        String sqlBook = "INSERT INTO book (book_id, title, public_id_image, url_image, quantity, description, publisherId, isEnable, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlBookAuthor = "INSERT INTO book_author (book_id, author_id) " +
                "VALUES (?, ?)";
        String sqlBookCategory = "INSERT INTO book_category (book_id, category_id) " +
                "VALUES (?, ?)";
        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement bookStmt = conn.prepareStatement(sqlBook);
            bookStmt.setString(1, book.getId());
            bookStmt.setString(2, book.getName());
            bookStmt.setString(3, book.getImage_public_id());
            bookStmt.setString(4, book.getImage_url());
            bookStmt.setInt(5, 0);
            bookStmt.setString(6, book.getDescription());
            bookStmt.setString(7, book.getPublisher().getId());
            bookStmt.setBoolean(8, false);
            bookStmt.setTimestamp(9, Timestamp.valueOf(book.getCreated_at()));
            bookStmt.setDate(10, null);
            bookStmt.executeUpdate();
            PreparedStatement bookAuthorStmt = conn.prepareStatement(sqlBookAuthor);
            for (AuthorEntity author : book.getAuthors()) {
                bookAuthorStmt.setString(1, book.getId());
                bookAuthorStmt.setString(2, author.getId());
                bookAuthorStmt.addBatch();
            }
            bookAuthorStmt.executeBatch();
            PreparedStatement bookCategoryStmt = conn.prepareStatement(sqlBookCategory);
            for (CategoryEntity category : book.getCategories()) {
                bookCategoryStmt.setString(1, book.getId());
                bookCategoryStmt.setString(2, category.getId());
                bookCategoryStmt.addBatch();
            }
            bookCategoryStmt.executeBatch();
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.closeConnection();
        }
    }
    @Override
    public boolean editBook(BookEntity book) {
        List<AuthorEntity> authorEntityList = book.getAuthors();
        List<CategoryEntity> categoryEntityList = book.getCategories();
        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);
            String updateBookSql = "UPDATE book SET title = ?, description = ?, updated_at = ?, publisherId = ?"
                    + (book.getImage_public_id() != null ? ", public_id_image = ?, url_image= ?": "")
                    + " WHERE book_id = ?";
            PreparedStatement updateBookPrt = conn.prepareStatement(updateBookSql);
            updateBookPrt.setString(1, book.getName());
            updateBookPrt.setString(2, book.getDescription());
            updateBookPrt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            updateBookPrt.setString(4, book.getPublisher().getId());
            if (book.getImage_public_id() != null) {
                updateBookPrt.setString(5, book.getImage_public_id());
                updateBookPrt.setString(6, book.getImage_url());
            }
            updateBookPrt.setString(book.getImage_public_id() != null ? 7 : 5, book.getId());
            updateBookPrt.executeUpdate();
            String deleteBookAuthor = "DELETE FROM book_author WHERE book_id = ?";
            PreparedStatement deleteBookAuthorPrt = conn.prepareStatement(deleteBookAuthor);
            deleteBookAuthorPrt.setString(1, book.getId());
            deleteBookAuthorPrt.executeUpdate();
            String insertBookAuthor = "INSERT INTO book_author (book_id, author_id) VALUES (?, ?)";
            PreparedStatement insertBookAuthorPrt = conn.prepareStatement(insertBookAuthor);
            for (AuthorEntity author: authorEntityList) {
                insertBookAuthorPrt.setString(1, book.getId());
                insertBookAuthorPrt.setString(2, author.getId());
                insertBookAuthorPrt.addBatch();
            }
            insertBookAuthorPrt.executeBatch();
            String deleteBookCateSql = "DELETE FROM book_category WHERE book_id = ?";
            PreparedStatement deleteBookCatePrt = conn.prepareStatement(deleteBookCateSql);
            deleteBookCatePrt.setString(1, book.getId());
            deleteBookCatePrt.executeUpdate();
            String insertBookCate = "INSERT INTO book_category (book_id, category_id) VALUES (?, ?)";
            PreparedStatement insertBookCatePrt = conn.prepareStatement(insertBookCate);
            for (CategoryEntity category: categoryEntityList) {
                insertBookCatePrt.setString(1, book.getId());
                insertBookCatePrt.setString(2, category.getId());
                insertBookCatePrt.addBatch();
            }
            insertBookCatePrt.executeBatch();
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public boolean lockOrUnLockBook(String id, Boolean enable) {
        return false;
    }

    @Override
    public BookEntity getBookDetail(String bookId) {
        BookEntity bookDetail = new BookEntity();
        PublisherEntity publisher = new PublisherEntity();
        List<AuthorEntity> authorEntityList = new ArrayList<>();
        List<CategoryEntity> categoryEntityList = new ArrayList<>();
        String sqlBook = "SELECT book_id, title, public_id_image, url_image, book.publisherId, book.description, publisher.name AS publisher_name" +
                " FROM book" +
                " JOIN publisher ON book.publisherId = publisher.publisher_id" +
                " WHERE book_id = ?";
        String sqlAuthor = "SELECT book_author.author_id, author.name" +
                " FROM book_author" +
                " JOIN author ON book_author.author_id = author.author_id" +
                " WHERE book_id = ?";
        String sqlCategory = "SELECT book_category.category_id, category.name" +
                " FROM book_category" +
                " JOIN category ON book_category.category_id = category.category_id" +
                " WHERE book_id = ?";
        try {
            db.initPrepar(sqlBook);
            db.getPreparedStatement().setString(1, bookId);
            resultSet = db.executeSelect();
            if (resultSet.next()) {
                bookDetail.setId(resultSet.getString("book_id"));
                bookDetail.setName(resultSet.getString("title"));
                bookDetail.setImage_public_id(resultSet.getString("public_id_image"));
                bookDetail.setImage_url(resultSet.getString("url_image"));
                bookDetail.setDescription(resultSet.getString("description"));
                publisher.setId(resultSet.getString("publisherId"));
                publisher.setName(resultSet.getString("publisher_name"));
                bookDetail.setPublisher(publisher);
            }
            db.initPrepar(sqlAuthor);
            db.getPreparedStatement().setString(1, bookId);
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                AuthorEntity author = new AuthorEntity();
                author.setId(resultSet.getString("author_id"));
                author.setName(resultSet.getString("name"));
                authorEntityList.add(author);
            }
            bookDetail.setAuthors(authorEntityList);
            db.initPrepar(sqlCategory);
            db.getPreparedStatement().setString(1, bookId);
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                CategoryEntity category = new CategoryEntity();
                category.setId(resultSet.getString("category_id"));
                category.setName(resultSet.getString("name"));
                categoryEntityList.add(category);
            }
            bookDetail.setCategories(categoryEntityList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return bookDetail;
    }
}
