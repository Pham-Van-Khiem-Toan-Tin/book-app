package com.book.app.Dao.impl;

import com.book.app.Config.DBConnection;
import com.book.app.Dao.PublisherDao;
import com.book.app.Entity.CategoryEntity;
import com.book.app.Entity.PublisherEntity;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PublisherDaoImpl implements PublisherDao {
    private DBConnection db = new DBConnection();
    private ResultSet resultSet;
    @Override
    public List<PublisherEntity> getAllPublisher(String keyword, String sort) {
        List<PublisherEntity> publisherEntityList = new ArrayList<>();
        String sql = "SELECT * FROM publisher";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql = sql + " WHERE  name LIKE '%" + keyword + "%' OR description LIKE '%" + keyword + "%' OR (CAST(created_at AS CHAR) LIKE '%" + keyword + "%')";
        }
        if (sort != null) {

        }
        try {
            db.initPrepar(sql);
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                PublisherEntity publisher = new PublisherEntity();
                publisher.setId(resultSet.getString("publisher_id"));
                publisher.setName(resultSet.getString("name"));
                publisher.setDescription(resultSet.getString("description"));
                int isEnableValue = resultSet.getInt("isEnable");
                Boolean isEnable = (isEnableValue == 1);
                publisher.setEnable(isEnable);
                publisher.setCreated_at(resultSet.getTimestamp("created_at").toLocalDateTime());
                publisherEntityList.add(publisher);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return publisherEntityList;
    }

    @Override
    public boolean addPublisher(PublisherEntity publisher) {
        String sql = "INSERT INTO publisher (publisher_id, name, description, isEnable, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            db.initPrepar(sql);
            db.getPreparedStatement().setString(1, publisher.getId());
            db.getPreparedStatement().setString(2, publisher.getName());
            db.getPreparedStatement().setString(3, publisher.getDescription());
            db.getPreparedStatement().setBoolean(4, publisher.getEnable());
            db.getPreparedStatement().setTimestamp(5, Timestamp.valueOf(publisher.getCreated_at()));
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
    public boolean editPublisher(PublisherEntity publisher) {
        String sql = "UPDATE publisher " +
                "SET name = ?, description = ?, updated_at = ? " +
                "WHERE publisher_id = ?";
        try {
            db.initPrepar(sql);
            // Thiết lập các tham số cho câu lệnh SQL
            db.getPreparedStatement().setString(1, publisher.getName());
            db.getPreparedStatement().setString(2, publisher.getDescription());
            db.getPreparedStatement().setTimestamp(3, Timestamp.valueOf(publisher.getUpdated_at()));
            db.getPreparedStatement().setString(4, publisher.getId());

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
    public boolean lockOrUnLockPublisher(String id, Boolean enable) {
        String sql = "UPDATE publisher " +
                "SET isEnable = ? " +
                "WHERE publisher_id = ?";
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
}
