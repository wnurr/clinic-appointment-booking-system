package com.clinic.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/clinic_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Oneknow26";

    public static Connection getConnection() {

        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }

        return conn;
    }
}