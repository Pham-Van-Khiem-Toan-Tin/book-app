package com.book.app.Dao;

import java.sql.*;

public class DBConnection {
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private int ok;

    public Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/bookstore";
        String user = "root";
        String password = "271201";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void initPrepar(String sql) {
        try {
            getConnection();
            preparedStatement = connection.prepareStatement(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeSelect() {
        resultSet = null;
        try {
            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

}
