package sms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String DB_URL = "DatabaseName";  // Replace with your MySQL database URL   
    private static final String DB_USER = "username";  // Replace with your MySQL username
    private static final String DB_PASSWORD = "DBpassword";  // Replace with your MySQL password

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
