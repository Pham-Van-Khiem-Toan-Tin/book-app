package com.book.app.Dao.impl;

import com.book.app.Dao.DBConnection;
import com.book.app.Dao.IUser;
import com.book.app.Entity.User;
import com.book.app.Utils.PasswordUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;

import java.sql.Date;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserImpl implements IUser {
    private DBConnection db = new DBConnection();
    private ResultSet resultSet;
    @Override
    public List<User> getAllUser() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try {
            db.initPrepar(sql);
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("userId"));
                user.setPassword(resultSet.getString("password"));
                user.setUsername(resultSet.getString("username"));
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                int isAdminValue = resultSet.getInt("isAdmin");
                // Chuyển đổi giá trị từ TINYINT sang boolean
                Boolean isAdmin = (isAdminValue == 1);
                user.setAdmin(isAdmin);
                user.setEmail(resultSet.getString("email"));
                int isEnableValue = resultSet.getInt("isEnable");
                Boolean isEnable = (isEnableValue == 1);
                user.setEnable(isEnable);
                user.setCreatedAt(resultSet.getDate("created_at").toLocalDate());
                userList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return userList;
    }

    @Override
    public boolean addUser(User user) {
        String sql = "INSERT INTO user (username, password, email, phone, address, isAdmin, isEnable, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            db.initPrepar(sql);
            db.getPreparedStatement().setString(1, user.getUsername());
            db.getPreparedStatement().setString(2, PasswordUtils.hashPassword(user.getPassword()));
            db.getPreparedStatement().setString(3, user.getEmail());
            db.getPreparedStatement().setString(4, user.getPhone());
            db.getPreparedStatement().setString(5, user.getAddress());
            db.getPreparedStatement().setBoolean(6, false);
            db.getPreparedStatement().setBoolean(7, true);
            db.getPreparedStatement().setDate(8, Date.valueOf(LocalDate.now()));
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
    public boolean editUser(User user) {
        String sql = "UPDATE user " +
                "SET username = ?, email = ?, phone = ?, address  = ?, isAdmin = ? " +
                "WHERE userId = ?";
        try {
            db.initPrepar(sql);
            // Thiết lập các tham số cho câu lệnh SQL
            db.getPreparedStatement().setString(1, user.getUsername());
            db.getPreparedStatement().setString(2, user.getEmail());
            db.getPreparedStatement().setString(3, user.getPhone());
            db.getPreparedStatement().setString(4, user.getAddress()); // Chuyển đổi Date sang java.sql.Date
            db.getPreparedStatement().setBoolean(5, user.getAdmin());
            db.getPreparedStatement().setInt(6, user.getId());

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
    public boolean lockOrUnLockUser(int id, Boolean enable) {
        String sql = "UPDATE user " +
                "SET isEnable = ? " +
                "WHERE userId = ?";
        try {
            db.initPrepar(sql);
            // Thiết lập các tham số cho câu lệnh SQL
            db.getPreparedStatement().setBoolean(1, enable);
            db.getPreparedStatement().setInt(2, id);

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
