package com.book.app.Dao.impl;

import com.book.app.Config.DBConnection;
import com.book.app.Dao.SaleDao;
import com.book.app.Entity.SaleEntity;

import java.sql.Date;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleDaoImpl implements SaleDao {
    private DBConnection db = new DBConnection();
    private ResultSet resultSet;

    @Override
    public List<SaleEntity> getAllRevenueOfBook(String type, LocalDate startDate, LocalDate endDate) {
        String sql = "";
        List<SaleEntity> saleEntityList = new ArrayList<>();
        if (startDate == null && endDate == null) {
            switch (type) {
                case "Day":
                    sql = "SELECT HOUR(o.created_at) AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "WHERE DATE(o.created_at) = CURDATE() " +
                            "GROUP BY HOUR(o.created_at) " +
                            "ORDER BY HOUR(o.created_at) ASC";
                    break;
                case "Week":
                    sql = "SELECT DATE(o.created_at) AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "WHERE o.created_at BETWEEN CURDATE() - INTERVAL 6 DAY AND CURDATE() + INTERVAL 1 DAY " +
                            "GROUP BY DATE(o.created_at) " +
                            "ORDER BY DATE(o.created_at) ASC";
                    break;
                case "Month":
                    sql = "SELECT DATE(o.created_at) AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "WHERE o.created_at BETWEEN CURDATE() - INTERVAL 30 DAY AND CURDATE() + INTERVAL 1 DAY " +
                            "GROUP BY DATE(o.created_at) " +
                            "ORDER BY DATE(o.created_at) ASC";
                    break;
                case "Year":
                    sql = "SELECT YEAR(o.created_at) AS year, MONTH(o.created_at) AS month, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "WHERE o.created_at >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                            "GROUP BY YEAR(o.created_at), MONTH(o.created_at) " +
                            "ORDER BY year ASC, month ASC";
                    break;
            }
        } else {
            sql = "SELECT DATE(o.created_at) AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                    "FROM orders o JOIN book_order oi ON o.order_id = oi.order_id  WHERE DATE(o.created_at) BETWEEN ? AND ? " +
                    "GROUP BY DATE(o.created_at) " +
                    "ORDER BY DATE(o.created_at) ASC";
        }
        try {
            db.initPrepar(sql);
            if (startDate != null && endDate != null) {
                db.getPreparedStatement().setDate(1, Date.valueOf(startDate));
                db.getPreparedStatement().setDate(2, Date.valueOf(endDate));
            }
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                if (type.equals("Year")) {
                    SaleEntity sale = new SaleEntity(resultSet.getString("year") + "-" + (Integer.parseInt(resultSet.getString("month")) < 10 ? "0" + resultSet.getString("month") : resultSet.getString("month")), resultSet.getDouble("revenue"));
                    saleEntityList.add(sale);
                } else {
                    SaleEntity sale = new SaleEntity(resultSet.getString("label"), resultSet.getDouble("revenue"));
                    saleEntityList.add(sale);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return saleEntityList;
    }

    @Override
    public List<SaleEntity> getAllRevenueOfAuthor(String type, LocalDate startDate, LocalDate endDate) {
        String sql = "";
        List<SaleEntity> saleEntityList = new ArrayList<>();
        if (startDate == null && endDate == null) {
            switch (type) {
                case "Day":
                    sql = "SELECT a.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "JOIN book_author ba ON b.book_id = ba.book_id " +
                            "JOIN author a ON ba.author_id = a.author_id " +
                            "WHERE DATE(o.created_at) = CURDATE() " +
                            "GROUP BY a.name " +
                            "ORDER BY revenue DESC";
                    break;
                case "Week":
                    sql = "SELECT a.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "JOIN book_author ba ON b.book_id = ba.book_id " +
                            "JOIN author a ON ba.author_id = a.author_id " +
                            "WHERE o.created_at BETWEEN CURDATE() - INTERVAL 6 DAY AND CURDATE() + INTERVAL 1 DAY " +
                            "GROUP BY a.name " +
                            "ORDER BY revenue DESC";
                    break;
                case "Month":
                    sql = "SELECT a.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "JOIN book_author ba ON b.book_id = ba.book_id " +
                            "JOIN author a ON ba.author_id = a.author_id " +
                            "WHERE o.created_at BETWEEN CURDATE() - INTERVAL 30 DAY AND CURDATE() + INTERVAL 1 DAY " +
                            "GROUP BY a.name " +
                            "ORDER BY revenue DESC";
                    break;
                case "Year":
                    sql = "SELECT a.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "JOIN book_author ba ON b.book_id = ba.book_id " +
                            "JOIN author a ON ba.author_id = a.author_id " +
                            "WHERE o.created_at >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                            "GROUP BY a.name " +
                            "ORDER BY revenue DESC";
                    break;
            }
        } else {
            sql = "SELECT a.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                    "FROM orders o " +
                    "JOIN book_order oi ON o.order_id = oi.order_id " +
                    "JOIN book b ON oi.book_id = b.book_id " +
                    "JOIN book_author ba ON b.book_id = ba.book_id " +
                    "JOIN author a ON ba.author_id = a.author_id " +
                    "WHERE DATE(o.created_at) BETWEEN ? AND ? " +
                    "GROUP BY a.name " +
                    "ORDER BY revenue DESC";
        }
        try {
            db.initPrepar(sql);
            if (startDate != null && endDate != null) {
                db.getPreparedStatement().setDate(1, Date.valueOf(startDate));
                db.getPreparedStatement().setDate(2, Date.valueOf(endDate));
            }
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                if (type.equals("Year")) {
                    SaleEntity sale = new SaleEntity(resultSet.getString("year") + "-" + (Integer.parseInt(resultSet.getString("month")) < 10 ? "0" + resultSet.getString("month") : resultSet.getString("month")), resultSet.getDouble("revenue"));
                    saleEntityList.add(sale);
                } else {
                    SaleEntity sale = new SaleEntity(resultSet.getString("label"), resultSet.getDouble("revenue"));
                    saleEntityList.add(sale);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return saleEntityList;
    }

    @Override
    public List<SaleEntity> getAllRevenueOfCategory(String type, LocalDate startDate, LocalDate endDate) {
        String sql = "";
        List<SaleEntity> saleEntityList = new ArrayList<>();
        if (startDate == null && endDate == null) {
            switch (type) {
                case "Day":
                    sql = "SELECT c.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "JOIN book_category bc ON b.book_id = bc.book_id " +
                            "JOIN category c ON bc.category_id = c.category_id " +
                            "WHERE DATE(o.created_at) = CURDATE() " +
                            "GROUP BY c.name " +
                            "ORDER BY revenue DESC";
                    break;
                case "Week":
                    sql = "SELECT c.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "JOIN book_category bc ON b.book_id = bc.book_id " +
                            "JOIN category c ON bc.category_id = c.category_id " +
                            "WHERE o.created_at BETWEEN CURDATE() - INTERVAL 6 DAY AND CURDATE() + INTERVAL 1 DAY " +
                            "GROUP BY c.name " +
                            "ORDER BY revenue DESC";
                    break;
                case "Month":
                    sql = "SELECT c.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "JOIN book_category bc ON b.book_id = bc.book_id " +
                            "JOIN category c ON bc.category_id = c.category_id " +
                            "WHERE o.created_at BETWEEN CURDATE() - INTERVAL 30 DAY AND CURDATE() + INTERVAL 1 DAY " +
                            "GROUP BY c.name " +
                            "ORDER BY revenue DESC";
                    break;
                case "Year":
                    sql = "SELECT c.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "JOIN book_category bc ON b.book_id = bc.book_id " +
                            "JOIN category c ON bc.category_id = c.category_id " +
                            "WHERE o.created_at >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                            "GROUP BY c.name " +
                            "ORDER BY revenue DESC";
                    break;
            }
        } else {
            sql = "SELECT c.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                    "FROM orders o " +
                    "JOIN book_order oi ON o.order_id = oi.order_id " +
                    "JOIN book b ON oi.book_id = b.book_id " +
                    "JOIN book_category bc ON b.book_id = bc.book_id " +
                    "JOIN category c ON bc.category_id = c.category_id " +
                    "WHERE DATE(o.created_at) BETWEEN ? AND ? " +
                    "GROUP BY c.name " +
                    "ORDER BY revenue DESC";
        }
        try {
            db.initPrepar(sql);
            if (startDate != null && endDate != null) {
                db.getPreparedStatement().setDate(1, Date.valueOf(startDate));
                db.getPreparedStatement().setDate(2, Date.valueOf(endDate));
            }
            resultSet = db.executeSelect();
            while (resultSet.next()) {

                SaleEntity sale = new SaleEntity(resultSet.getString("label"), resultSet.getDouble("revenue"));
                saleEntityList.add(sale);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return saleEntityList;
    }

    @Override
    public List<SaleEntity> getAllRevenueOfPublisher(String type, LocalDate startDate, LocalDate endDate) {
        String sql = "";
        List<SaleEntity> saleEntityList = new ArrayList<>();
        if (startDate == null && endDate == null) {
            switch (type) {
                case "Day":
                    sql = "SELECT p.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "JOIN publisher p ON b.publisherId = p.publisher_id  " +
                            "WHERE DATE(o.created_at) = CURDATE() " +
                            "GROUP BY p.name " +
                            "ORDER BY revenue DESC";
                    break;
                case "Week":
                    sql = "SELECT p.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "JOIN publisher p ON b.publisherId = p.publisher_id  " +
                            "WHERE o.created_at BETWEEN CURDATE() - INTERVAL 6 DAY AND CURDATE() + INTERVAL 1 DAY " +
                            "GROUP BY p.name " +
                            "ORDER BY revenue DESC";
                    break;
                case "Month":
                    sql = "SELECT p.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "JOIN publisher p ON b.publisherId = p.publisher_id  " +
                            "WHERE o.created_at BETWEEN CURDATE() - INTERVAL 30 DAY AND CURDATE() + INTERVAL 1 DAY " +
                            "GROUP BY p.name " +
                            "ORDER BY revenue DESC";
                    break;
                case "Year":
                    sql = "SELECT p.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "JOIN publisher p ON b.publisherId = p.publisher_id  " +
                            "WHERE o.created_at >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                            "GROUP BY p.name " +
                            "ORDER BY revenue DESC";
                    break;
            }
        } else {
            sql = "SELECT p.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                    "FROM orders o " +
                    "JOIN book_order oi ON o.order_id = oi.order_id " +
                    "JOIN book b ON oi.book_id = b.book_id " +
                    "JOIN publisher p ON b.publisherId = p.publisher_id  " +
                    "WHERE DATE(o.created_at) BETWEEN ? AND ? " +
                    "GROUP BY p.name " +
                    "ORDER BY revenue DESC";
        }
        try {
            db.initPrepar(sql);
            if (startDate != null && endDate != null) {
                db.getPreparedStatement().setDate(1, Date.valueOf(startDate));
                db.getPreparedStatement().setDate(2, Date.valueOf(endDate));
            }
            resultSet = db.executeSelect();
            while (resultSet.next()) {

                SaleEntity sale = new SaleEntity(resultSet.getString("label"), resultSet.getDouble("revenue"));
                saleEntityList.add(sale);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return saleEntityList;
    }

    @Override
    public List<SaleEntity> getAllRevenueOfEmployee(String type, LocalDate startDate, LocalDate endDate) {
        String sql = "";
        List<SaleEntity> saleEntityList = new ArrayList<>();
        if (startDate == null && endDate == null) {
            switch (type) {
                case "Day":
                    sql = "SELECT u.username AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN user u ON o.user_id = u.userId " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "WHERE DATE(o.created_at) = CURDATE() " +
                            "GROUP BY u.username " +
                            "ORDER BY revenue DESC";
                    break;
                case "Week":
                    sql = "SELECT u.username AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN user u ON o.user_id = u.userId " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "WHERE o.created_at BETWEEN CURDATE() - INTERVAL 6 DAY AND CURDATE() + INTERVAL 1 DAY " +
                            "GROUP BY u.username " +
                            "ORDER BY revenue DESC";
                    break;
                case "Month":
                    sql = "SELECT u.username AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN user u ON o.user_id = u.userId " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "WHERE o.created_at BETWEEN CURDATE() - INTERVAL 30 DAY AND CURDATE() + INTERVAL 1 DAY " +
                            "GROUP BY u.username " +
                            "ORDER BY revenue DESC";
                    break;
                case "Year":
                    sql = "SELECT u.username AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                            "FROM orders o " +
                            "JOIN user u ON o.user_id = u.userId " +
                            "JOIN book_order oi ON o.order_id = oi.order_id " +
                            "JOIN book b ON oi.book_id = b.book_id " +
                            "WHERE o.created_at >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                            "GROUP BY u.username " +
                            "ORDER BY revenue DESC";
                    break;
            }
        } else {
            sql = "SELECT p.name AS label, SUM(oi.quantity * oi.price - oi.cost) AS revenue " +
                    "FROM orders o " +
                    "JOIN book_order oi ON o.order_id = oi.order_id " +
                    "JOIN book b ON oi.book_id = b.book_id " +
                    "JOIN publisher p ON b.publisher_id = p.publisher_id  " +
                    "WHERE DATE(o.created_at) BETWEEN ? AND ? " +
                    "GROUP BY p.name " +
                    "ORDER BY revenue DESC";
        }
        try {
            db.initPrepar(sql);
            if (startDate != null && endDate != null) {
                db.getPreparedStatement().setDate(1, Date.valueOf(startDate));
                db.getPreparedStatement().setDate(2, Date.valueOf(endDate));
            }
            resultSet = db.executeSelect();
            while (resultSet.next()) {

                SaleEntity sale = new SaleEntity(resultSet.getString("label"), resultSet.getDouble("revenue"));
                saleEntityList.add(sale);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return saleEntityList;
    }
}
