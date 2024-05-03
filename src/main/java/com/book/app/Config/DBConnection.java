package com.book.app.Config;

import io.github.cdimascio.dotenv.Dotenv;


import java.sql.*;

public class DBConnection {
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private int ok;

    public Connection getConnection() {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DATABASE_URL");
        String user = dotenv.get("DATABASE_USER");
        String password = dotenv.get("DATABASE_PASSWORD");
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void initPrepar(String sql) throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                getConnection();
            }
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
