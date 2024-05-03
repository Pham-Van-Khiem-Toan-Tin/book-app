package com.book.app.Dao.impl;

import com.book.app.Config.DBConnection;
import com.book.app.Dao.IEmployeeDao;
import com.book.app.Entity.EmployeeEntity;
import com.book.app.Utils.PasswordUtils;

import java.sql.Date;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDaoImpl implements IEmployeeDao {
    private DBConnection db = new DBConnection();
    private ResultSet resultSet;

    @Override
    public EmployeeEntity login(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        try {

            db.initPrepar(sql);
            db.getPreparedStatement().setString(1, username);
            db.getPreparedStatement().setString(2, PasswordUtils.hashPassword(password));
            resultSet = db.executeSelect();
            int count = 0;
            EmployeeEntity user = null;
            while (resultSet.next()) {
                count++;
                if (count > 1) {
                    // Nếu tìm thấy nhiều hơn một bản ghi, trả về null hoặc xử lý theo cách khác tùy thuộc vào yêu cầu của bạn
                    return null;
                }
                user = new EmployeeEntity();
                user.setId(resultSet.getString("userId"));
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
                user.setCreated_at(resultSet.getDate("created_at").toLocalDate());

            }
            return user; // Trả về đối tượng User nếu thông tin đăng nhập hợp lệ
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return null;
    }

    @Override
    public List<EmployeeEntity> getAllEmployee() {
        List<EmployeeEntity> userList = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try {
            db.initPrepar(sql);
            resultSet = db.executeSelect();
            while (resultSet.next()) {
                EmployeeEntity user = new EmployeeEntity();
                user.setId(resultSet.getString("userId"));
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
                user.setCreated_at(resultSet.getDate("created_at").toLocalDate());
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
    public boolean addEmployee(EmployeeEntity user) {
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
    public boolean editEmployee(EmployeeEntity user) {
        String sql = "UPDATE user " +
                "SET username = ?, email = ?, phone = ?, address  = ?, isAdmin = ?, updated_at = ? " +
                "WHERE userId = ?";
        try {
            db.initPrepar(sql);
            // Thiết lập các tham số cho câu lệnh SQL
            db.getPreparedStatement().setString(1, user.getUsername());
            db.getPreparedStatement().setString(2, user.getEmail());
            db.getPreparedStatement().setString(3, user.getPhone());
            db.getPreparedStatement().setString(4, user.getAddress()); // Chuyển đổi Date sang java.sql.Date
            db.getPreparedStatement().setBoolean(5, user.getAdmin());
            db.getPreparedStatement().setDate(6, Date.valueOf(user.getUpdatedAt()));
            db.getPreparedStatement().setString(7, user.getId());

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
    public boolean lockOrUnLockEmployee(String id, Boolean enable) {
        String sql = "UPDATE user " +
                "SET isEnable = ? " +
                "WHERE userId = ?";
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
    public boolean resetPassword(String id, String newPassword) {
        String sql = "UPDATE user " +
                "SET password = ? " +
                "WHERE userId = ?";
        try {
            db.initPrepar(sql);
            // Thiết lập các tham số cho câu lệnh SQL
            db.getPreparedStatement().setString(1, newPassword);
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
