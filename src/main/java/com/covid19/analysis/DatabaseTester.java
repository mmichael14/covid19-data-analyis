package com.covid19.analysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Comprehensive database connection tester
 * Identifies exact connection issues
 */
public class DatabaseTester {
    
    public static void main(String[] args) {
        System.out.println("DATABASE CONNECTION DIAGNOSTICS");
        System.out.println("================================");
        
        testAllConnectionMethods();
    }
    
    public static void testAllConnectionMethods() {
        String[] connectionURLs = {
            // Try different connection methods
            "jdbc:sqlserver://localhost:1433;databaseName=Covid19Analysis;integratedSecurity=true;",
            "jdbc:sqlserver://localhost;databaseName=Covid19Analysis;integratedSecurity=true;",
            "jdbc:sqlserver://.;databaseName=Covid19Analysis;integratedSecurity=true;",
            "jdbc:sqlserver://127.0.0.1;databaseName=Covid19Analysis;integratedSecurity=true;",
            "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=Covid19Analysis;integratedSecurity=true;",
            "jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=Covid19Analysis;integratedSecurity=true;"
        };
        
        String[] descriptions = {
            "Port 1433 with Windows Auth",
            "Default port with Windows Auth", 
            "Local machine with Windows Auth",
            "IP address with Windows Auth",
            "SQL Express instance",
            "SQL Express with instance name"
        };
        
        for (int i = 0; i < connectionURLs.length; i++) {
            testConnection(descriptions[i], connectionURLs[i]);
        }
    }
    
    private static void testConnection(String description, String url) {
        System.out.println("\n--- Testing: " + description + " ---");
        System.out.println("URL: " + url);
        
        try {
            // Load driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("SUCCESS: JDBC Driver loaded");
            
            // Test connection
            Connection conn = DriverManager.getConnection(url);
            System.out.println("SUCCESS: Connected to database!");
            
            // Test basic query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM Regions");
            if (rs.next()) {
                System.out.println("SUCCESS: Regions table exists: " + rs.getInt("total") + " records");
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as total FROM DailyStats");
            if (rs.next()) {
                System.out.println("SUCCESS: DailyStats table exists: " + rs.getInt("total") + " records");
            }
            
            conn.close();
            System.out.println("SUCCESS: Connection closed properly");
            
        } catch (ClassNotFoundException e) {
            System.err.println("FAILED: JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("FAILED: Connection failed: " + e.getMessage());
            System.err.println("Error code: " + e.getErrorCode());
            System.err.println("SQL state: " + e.getSQLState());
        }
    }
}