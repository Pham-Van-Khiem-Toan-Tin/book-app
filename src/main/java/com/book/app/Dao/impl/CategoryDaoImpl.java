package com.book.app.Dao.impl;

import com.book.app.Config.DBConnection;
import com.book.app.Dao.CategoryDao;
import com.book.app.Entity.AuthorEntity;
import com.book.app.Entity.CategoryEntity;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoImpl implements CategoryDao {
    private DBConnection db = new DBConnection();
    private List<CategoryEntity> categoryEntityList = new ArrayList<>();
    private ResultSet resultSet;
    @Override
    public List<CategoryEntity> getAllCategory(String keyword, String sort) {
        String sql = "SELECT * FROM category";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql = sql + " WHERE  name LIKE '%" + keyword + "%' OR description LIKE '%" + keyword + "%' OR (CAST(created_at AS CHAR) LIKE '%" + keyword + "%')";
        }
        if (sort != null) {

        }
        try {
            db.initPrepar(sql);
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                CategoryEntity category = new CategoryEntity();
                category.setId(resultSet.getString("category_id"));
                category.setName(resultSet.getString("name"));
                category.setDescription(resultSet.getString("description"));
                int isEnableValue = resultSet.getInt("isEnable");
                Boolean isEnable = (isEnableValue == 1);
                category.setEnable(isEnable);
                category.setCreated_at(resultSet.getTimestamp("created_at").toLocalDateTime());
                categoryEntityList.add(category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return categoryEntityList;
    }

    @Override
    public boolean addCategory(CategoryEntity category) {
        String sql = "INSERT INTO category (category_id, name, description, isEnable, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            db.initPrepar(sql);
            db.getPreparedStatement().setString(1, category.getId());
            db.getPreparedStatement().setString(2, category.getName());
            db.getPreparedStatement().setString(3, category.getDescription());
            db.getPreparedStatement().setBoolean(4, category.getEnable());
            db.getPreparedStatement().setTimestamp(5, Timestamp.valueOf(category.getCreated_at()));
            db.getPreparedStatement().setDate(6, null);
            db.getPreparedStatement().executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public boolean editCategory(CategoryEntity category) {
        String sql = "UPDATE category " +
                "SET name = ?, description = ?, updated_at = ? " +
                "WHERE category_id = ?";
        try {
            db.initPrepar(sql);
            // Thiết lập các tham số cho câu lệnh SQL
            db.getPreparedStatement().setString(1, category.getName());
            db.getPreparedStatement().setString(2, category.getDescription());
            db.getPreparedStatement().setTimestamp(3, Timestamp.valueOf(category.getUpdated_at()));
            db.getPreparedStatement().setString(4, category.getId());

            // Thực thi truy vấn
            int rowsUpdated = db.getPreparedStatement().executeUpdate();
            System.out.println("row updated: " + rowsUpdated);
            // Trả về true nếu cập nhật thành công ít nhất một hàng
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            // Trả về false nếu có lỗi xảy ra khi cập nhật
            return false;
        } finally {
            db.closeConnection(); // Đảm bảo kết nối được đóng sau khi sử dụng xong
        }
    }

    @Override
    public boolean lockOrUnLockCategory(String id, Boolean enable) {
        String sql = "UPDATE category " +
                "SET isEnable = ? " +
                "WHERE category_id = ?";
        try {
            db.initPrepar(sql);
            // Thiết lập các tham số cho câu lệnh SQL
            db.getPreparedStatement().setBoolean(1, enable);
            db.getPreparedStatement().setString(2, id);

            // Thực thi truy vấn
            int rowsUpdated = db.getPreparedStatement().executeUpdate();
            System.out.println("row updated: " + rowsUpdated);
            // Trả về true nếu cập nhật thành công ít nhất một hàng
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            // Trả về false nếu có lỗi xảy ra khi cập nhật
            return false;
        } finally {
            db.closeConnection(); // Đảm bảo kết nối được đóng sau khi sử dụng xong
        }
    }

    @Override
    public List<CategoryEntity> getAllCategoryName() {
        String sql = "SELECT category_id, name FROM category WHERE isEnable = 1";
        try {
            db.initPrepar(sql);
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                CategoryEntity category = new CategoryEntity();
                category.setId(resultSet.getString("category_id"));
                category.setName(resultSet.getString("name"));
                categoryEntityList.add(category);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return categoryEntityList;
    }

    @Override
    public List<CategoryEntity> getAllCategoryOfBook(String bookId) {
        return null;
    }
}
