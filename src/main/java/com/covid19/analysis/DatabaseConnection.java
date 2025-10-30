package com.covid19.analysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Use shared memory connection - works without TCP/IP configuration
    private static final String URL = "jdbc:sqlserver://localhost;databaseName=Covid19Analysis;integratedSecurity=true;";
    
    private static Connection connection;
    
    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("JDBC Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load JDBC Driver: " + e.getMessage());
        }
    }
    
    private DatabaseConnection() {}
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL);
                System.out.println("Database connected successfully!");
            } catch (SQLException e) {
                System.err.println("Database connection failed: " + e.getMessage());
                System.err.println("Trying alternative connection methods...");
                
                // Try alternative connection strings
                tryAlternativeConnections();
                throw e;
            }
        }
        return connection;
    }
    
    private static void tryAlternativeConnections() {
        String[] alternativeURLs = {
            "jdbc:sqlserver://.;databaseName=Covid19Analysis;integratedSecurity=true;",
            "jdbc:sqlserver:localhost;databaseName=Covid19Analysis;integratedSecurity=true;",
            "jdbc:sqlserver://127.0.0.1;databaseName=Covid19Analysis;integratedSecurity=true;"
        };
        
        for (String url : alternativeURLs) {
            try {
                Connection testConn = DriverManager.getConnection(url);
                System.out.println("SUCCESS with alternative URL: " + url);
                testConn.close();
                break;
            } catch (SQLException e) {
                System.err.println("Failed with: " + url);
            }
        }
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("🎉 Database connection test PASSED!");
        } catch (SQLException e) {
            System.err.println("💥 Database connection test FAILED!");
        }
    }
}