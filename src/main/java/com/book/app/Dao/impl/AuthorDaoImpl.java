package com.book.app.Dao.impl;

import com.book.app.Config.DBConnection;
import com.book.app.Dao.AuthorDao;
import com.book.app.Entity.AuthorEntity;
import com.book.app.Entity.EmployeeEntity;
import com.book.app.Utils.PasswordUtils;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuthorDaoImpl implements AuthorDao {
    private DBConnection db = new DBConnection();
    private ResultSet resultSet;

    @Override
    public List<AuthorEntity> getAllAuthor(String keyword, String sort) {
        List<AuthorEntity> authorEntityList = new ArrayList<>();
        String sql = "SELECT * FROM author";
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql = sql + " WHERE  name LIKE '%" + keyword + "%' OR description LIKE '%" + keyword + "%' OR (CAST(created_at AS CHAR) LIKE '%" + keyword + "%')";
        }
        if (sort != null) {

        }
        try {
            db.initPrepar(sql);
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                AuthorEntity author = new AuthorEntity();
                author.setId(resultSet.getString("author_id"));
                author.setImage_url(resultSet.getString("image_url"));
                author.setImage_public_id(resultSet.getString("image_public_id"));
                author.setName(resultSet.getString("name"));
                author.setDescription(resultSet.getString("description"));
                int isEnableValue = resultSet.getInt("isEnable");
                Boolean isEnable = (isEnableValue == 1);
                author.setEnable(isEnable);
                author.setCreated_at(resultSet.getTimestamp("created_at").toLocalDateTime());
                authorEntityList.add(author);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return authorEntityList;
    }

    @Override
    public boolean addAuthor(AuthorEntity author) {
        String sql = "INSERT INTO author (author_id, name, image_url, image_public_id, description, isEnable, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            db.initPrepar(sql);
            db.getPreparedStatement().setString(1, author.getId());
            db.getPreparedStatement().setString(2, author.getName());
            db.getPreparedStatement().setString(3, author.getImage_url());
            db.getPreparedStatement().setString(4, author.getImage_public_id());
            db.getPreparedStatement().setString(5, author.getDescription());
            db.getPreparedStatement().setBoolean(6, author.getEnable());
            db.getPreparedStatement().setTimestamp(7, Timestamp.valueOf(author.getCreated_at()));
            db.getPreparedStatement().setDate(8, null);
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
    public boolean editAuthor(AuthorEntity author) {
        String sql = "UPDATE author " +
                "SET name = ?, description = ?, image_url = ?, image_public_id = ? " +
                "WHERE author_id = ?";
        try {
            db.initPrepar(sql);
            // Thiết lập các tham số cho câu lệnh SQL
            db.getPreparedStatement().setString(1, author.getName());
            db.getPreparedStatement().setString(2, author.getDescription());
            db.getPreparedStatement().setString(3, author.getImage_url());
            db.getPreparedStatement().setString(4, author.getImage_public_id());
            db.getPreparedStatement().setString(5, author.getId());


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
    public boolean lockOrUnLockAuthor(String id, Boolean enable) {
        String sql = "UPDATE author " +
                "SET isEnable = ? " +
                "WHERE author_id = ?";
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
    public List<AuthorEntity> getAllAuthorName() {
        List<AuthorEntity> authorEntityList = new ArrayList<>();
        String sql = "SELECT author_id, name FROM author";
        try {
            db.initPrepar(sql);
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                AuthorEntity author = new AuthorEntity();
                author.setId(resultSet.getString("author_id"));
                author.setName(resultSet.getString("name"));
                authorEntityList.add(author);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return authorEntityList;
    }

    @Override
    public List<AuthorEntity> getAllAuthorOfBook(String bookId) {
        return null;
    }
}
