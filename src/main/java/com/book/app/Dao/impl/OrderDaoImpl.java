package com.book.app.Dao.impl;

import com.book.app.Config.DBConnection;
import com.book.app.Dao.OrderDao;
import com.book.app.Entity.*;
import com.book.app.Utils.AppUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class OrderDaoImpl implements OrderDao {
    private DBConnection db = new DBConnection();
    private ResultSet resultSet;
    @Override
    public List<OrderEntity> getAllOrder(String keyword, String sort) {
        String sql = "SELECT order_id, quantity, total, created_at FROM orders WHERE user_id = ?";
//        if (keyword != null && !keyword.trim().isEmpty()) {
//            sql = sql + " WHERE  name LIKE '%" + keyword + "%' OR description LIKE '%" + keyword + "%' OR (CAST(created_at AS CHAR) LIKE '%" + keyword + "%')";
//        }
//        if (sort != null) {
//
//        }
        List<OrderEntity> orderEntityList = new ArrayList<>();
        try {
            db.initPrepar(sql);
            db.getPreparedStatement().setString(1, AppUtils.getId());
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                OrderEntity order = new OrderEntity();
                order.setId(resultSet.getString("order_id"));
                order.setQuantity(resultSet.getInt("quantity"));
                order.setTotal(resultSet.getDouble("total"));
                order.setCreated_at(resultSet.getTimestamp("created_at").toLocalDateTime());
                orderEntityList.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return orderEntityList;
    }

    @Override
    public boolean addOrder(List<BookEntity> bookEntityList, CustomerEntity customer) {
        String sqlOrder = "INSERT INTO orders (order_id, user_id, customer_id, quantity, cost, total, profit, created_at ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlOrderItem = "INSERT INTO book_order (book_id, order_id, quantity, price, cost) " +
                "VALUES (?, ?, ?, ?, ?)";
        String sqlOrderCustomer = "INSERT INTO customer (phone, name, email, created_at) " +
                "VALUES ( ?, ?, ?, ?)";
        String sqlFindCustomer = "SELECT * FROM customer WHERE phone = ?";
        try {
            Connection conn = db.getConnection();
            conn.setAutoCommit(false);
            String cutomerId = "anonymous_customer";
            String orderId = "order-" + AppUtils.getId() + "-" + System.currentTimeMillis();
            if (customer != null) {
                PreparedStatement customerExits = conn.prepareStatement(sqlFindCustomer);
                customerExits.setString(1, customer.getPhoneNumber());
                resultSet = customerExits.executeQuery();
                if (resultSet.next()) {
                    cutomerId = resultSet.getString("phone");
                } else {
                    PreparedStatement customerStmt = conn.prepareStatement(sqlOrderCustomer);
                    customerStmt.setString(1, customer.getPhoneNumber());
                    customerStmt.setString(2, customer.getName());
                    customerStmt.setString(3, customer.getEmail());
                    customerStmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    customerStmt.executeUpdate();
                    cutomerId = customer.getPhoneNumber();
                }
            }
            AtomicInteger quantity = new AtomicInteger();
            AtomicReference<Double> cost = new AtomicReference<>(0.0);
            AtomicReference<Double> total = new AtomicReference<>(0.0);
            double profit = 0.0;
            bookEntityList.stream().forEach(item -> {
                quantity.addAndGet(item.getQuantity());
                cost.updateAndGet(v -> new Double((double) (v + item.getCost() * item.getQuantity())));
                total.updateAndGet(v -> new Double((double) (v + item.getPrice() * item.getQuantity())));
            });
            profit = total.get() - cost.get();
            PreparedStatement orderStmt = conn.prepareStatement(sqlOrder);
            orderStmt.setString(1, orderId);
            orderStmt.setString(2, AppUtils.getId());
            orderStmt.setString(3, cutomerId);
            orderStmt.setInt(4, quantity.get());
            orderStmt.setDouble(5, cost.get());
            orderStmt.setDouble(6, total.get());
            orderStmt.setDouble(7, profit);
            orderStmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            orderStmt.executeUpdate();
            PreparedStatement orderItemStmt = conn.prepareStatement(sqlOrderItem);
            for (BookEntity book : bookEntityList) {
                orderItemStmt.setString(1, book.getId());
                orderItemStmt.setString(2, orderId);
                orderItemStmt.setInt(3, book.getQuantity());
                orderItemStmt.setDouble(4, book.getPrice());
                orderItemStmt.setDouble(5, book.getCost());
                orderItemStmt.addBatch();
            }
            orderItemStmt.executeBatch();
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
    public List<BookEntity> getBookToOrder(List<String> listBookSelected) {
        String sql = "SELECT book.book_id, book.title, book.quantity, book.url_image, publisher.name AS publisher, " +
                "CASE WHEN book.price IS NULL THEN ( SELECT imported_books.cost " +
                "FROM book_imported_sheet " +
                "JOIN imported_books ON book_imported_sheet.imported_sheet_id = imported_books.imported_sheet_id " +
                "WHERE imported_books.stoke > 0 " +
                "AND book.book_id = imported_books.book_id " +
                "ORDER BY book_imported_sheet.created_at ASC LIMIT 1 ) " +
                "ELSE book.price END AS cost " +
                "FROM book " +
                "JOIN book_author ON book.book_id = book_author.book_id " +
                "JOIN author ON book_author.author_id = author.author_id " +
                "JOIN publisher ON book.publisherId = publisher.publisher_id " +
                "JOIN book_category ON book.book_id = book_category.book_id " +
                "JOIN category ON book_category.category_id = category.category_id " +
                "WHERE book.quantity > 0 " +
                "AND book.isEnable = 1 AND author.isEnable = 1 AND publisher.isEnable = 1 AND category.isEnable = 1 ";
        if(listBookSelected.size() > 0) {
            sql += "AND book.book_id NOT IN ( ";
            for (int i = 0; i < listBookSelected.size(); i++) {
                sql += "?,";
            }
            sql = sql.substring(0, sql.length() - 1);
            sql += " ) ";
        }
        sql += "GROUP BY book.book_id, book.title, book.quantity, publisher.name, book.url_image, book.price";
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
                book.setCost(resultSet.getDouble("cost"));
                book.setPrice(1.1 * resultSet.getDouble("cost"));
//                book.setStock(resultSet.getInt("stock"));
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
