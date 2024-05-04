package com.book.app.Dao.impl;

import com.book.app.Config.DBConnection;
import com.book.app.Dao.InventoryDao;
import com.book.app.Entity.BookEntity;
import com.book.app.Entity.InventoryEntity;
import com.book.app.Entity.PublisherEntity;
import com.book.app.Utils.AppUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class InventoryDaoImpl implements InventoryDao {
    private DBConnection db = new DBConnection();
    private ResultSet resultSet;

    @Override
    public List<InventoryEntity> getAllInventory(String keyword, String sort) {
        String sql = "SELECT imported_sheet_id, quantity_book, total_cost, created_at FROM book_imported_sheet WHERE user_id = ?";
//        if (keyword != null && !keyword.trim().isEmpty()) {
//            sql = sql + " WHERE  name LIKE '%" + keyword + "%' OR description LIKE '%" + keyword + "%' OR (CAST(created_at AS CHAR) LIKE '%" + keyword + "%')";
//        }
//        if (sort != null) {
//
//        }
        List<InventoryEntity> inventoryList = new ArrayList<>();
        try {
            db.initPrepar(sql);
            db.getPreparedStatement().setString(1, AppUtils.getId());
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                InventoryEntity inventory = new InventoryEntity();
                inventory.setId(resultSet.getString("imported_sheet_id"));
                inventory.setQuantityBook(resultSet.getInt("quantity_book"));
                inventory.setTotalCost(resultSet.getDouble("total_cost"));
                inventory.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                inventoryList.add(inventory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return inventoryList;
    }

    @Override
    public boolean addInventory(List<BookEntity> bookEntityList) {
        String inventory_sheet_sql = "INSERT INTO book_imported_sheet (imported_sheet_id, user_id, quantity_book, total_cost, created_at) " +
                "VALUES (?, ?, ?, ?, ?)";
        String inventory_item_sql = "INSERT INTO imported_books (imported_sheet_id, book_id, stoke, quantity, cost) " +
                "VALUES (?, ?, ?, ?, ?)";
        String quantity_book_sql = "UPDATE book " +
                "SET quantity = quantity + ? " +
                "WHERE book_id = ?";
        try {
            AtomicReference<Double> total = new AtomicReference<>(0.0);
            AtomicReference<Integer> quantity = new AtomicReference<>(0);
            bookEntityList.stream().forEach(item -> {
                quantity.updateAndGet(v -> v + item.getQuantity());
                total.updateAndGet(v -> v + item.getCost() * item.getQuantity());
            });
            String idSheet = "sheet-" + AppUtils.getId() + "-" + System.currentTimeMillis();
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement sheetStmt = conn.prepareStatement(inventory_sheet_sql);
            sheetStmt.setString(1, idSheet);
            sheetStmt.setString(2, AppUtils.getId());
            sheetStmt.setInt(3, quantity.get());
            sheetStmt.setDouble(4, total.get());
            sheetStmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            sheetStmt.executeUpdate();
            PreparedStatement itemOfSheetStmt = conn.prepareStatement(inventory_item_sql);
            PreparedStatement quantityBookStmt = conn.prepareStatement(quantity_book_sql);
            for (BookEntity book : bookEntityList) {
                itemOfSheetStmt.setString(1, idSheet);
                itemOfSheetStmt.setString(2, book.getId());
                itemOfSheetStmt.setInt(3, book.getQuantity());
                itemOfSheetStmt.setInt(4, book.getQuantity());
                itemOfSheetStmt.setDouble(5, book.getCost());
                itemOfSheetStmt.addBatch();
                quantityBookStmt.setInt(1, book.getQuantity());
                quantityBookStmt.setString(2, book.getId());
                quantityBookStmt.addBatch();
            }
            itemOfSheetStmt.executeBatch();
            quantityBookStmt.executeBatch();
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<BookEntity> getBookToInventory(List<String> listBookSelected) {
        String sql = "SELECT b.book_id, b.title, b.url_image, p.name AS publisher" +
                " FROM book b" +
                " JOIN publisher p ON b.publisherId = p.publisher_id";
        if (listBookSelected.size() > 0) {
            sql += " WHERE b.book_id NOT IN (";
            for (int i = 0; i < listBookSelected.size(); i++) {
                sql += " ?,";
            }
            sql = sql.substring(0, sql.length() - 1); // Xóa dấu phẩy cuối cùng
            sql += " )";
        }
        List<BookEntity> listBook = new ArrayList<>();
        try {
            db.initPrepar(sql);
            if (listBookSelected.size() > 0) {
                for (int i = 0; i < listBookSelected.size(); i++) {
                    db.getPreparedStatement().setString(i + 1, listBookSelected.get(i));
                }
            }
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                BookEntity book = new BookEntity();
                PublisherEntity publisher = new PublisherEntity();
                book.setId(resultSet.getString("book_id"));
                book.setName(resultSet.getString("title"));
                publisher.setName(resultSet.getString("publisher"));
                book.setPublisher(publisher);
                book.setImage_url(resultSet.getString("url_image"));
                listBook.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return listBook;
    }
}
