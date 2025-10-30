package com.covid19.analysis;

/**
 * Simple test to verify our database connection works
 */
public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        DatabaseConnection.testConnection();
    }
}