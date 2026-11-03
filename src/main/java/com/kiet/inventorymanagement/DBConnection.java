
package com.kiet.inventorymanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/retail_db";

    public static Connection getConnection() throws SQLException {
        String url = System.getenv().getOrDefault("DB_URL", DEFAULT_URL);
        String user = System.getenv().getOrDefault("DB_USER", "root");
        String password = System.getenv("DB_PASSWORD");

        if (password == null) {
            throw new SQLException(
                    "DB_PASSWORD is not set. Configure it in the environment "
                            + "before starting the application."
            );
        }

        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("Connected to "
                    + connection.getMetaData().getDatabaseProductName() + ".");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }
}
