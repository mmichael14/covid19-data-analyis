package com.covid19.analysis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

/**
 * Enhanced COVID-19 Data Analysis GUI with Interactive Features
 */
public class CovidAnalysisGUI extends JFrame {
    private CSVReader csvReader;
    private List<String[]> covidData;
    
    // GUI Components
    private JTabbedPane tabbedPane;
    private JTextArea resultsArea;
    private JTable dataTable;
    
    // Buttons for event handling
    private JButton totalCasesBtn, fatalityRateBtn, movingAvgBtn, regionalBtn;
    
    public CovidAnalysisGUI() {
        csvReader = new CSVReader();
        covidData = csvReader.readCSV("data/daily_stats.csv");
        
        initializeGUI();
        setupEventHandlers();
    }
    
    private void initializeGUI() {
        setTitle("COVID-19 Data Analysis Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Create main tabbed interface
        tabbedPane = new JTabbedPane();
        
        // Add different panels as tabs
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Statistics", createStatisticsPanel());
        tabbedPane.addTab("Data View", createDataPanel());
        tabbedPane.addTab("Analysis", createAnalysisPanel());
        
        add(tabbedPane);
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Welcome message
        JLabel welcomeLabel = new JLabel(
            "<html><h1>COVID-19 Data Analysis Dashboard</h1>" +
            "<p>Interactive Analysis of Regional COVID-19 Statistics</p></html>",
            SwingConstants.CENTER
        );
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(new Color(0, 70, 130));
        panel.add(welcomeLabel, BorderLayout.NORTH);
        
        // Quick stats button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setBackground(new Color(240, 245, 250));
        
        // Create styled buttons
        totalCasesBtn = createStyledButton("Show Total Cases", new Color(70, 130, 180));
        fatalityRateBtn = createStyledButton("Calculate Fatality Rate", new Color(220, 80, 80));
        movingAvgBtn = createStyledButton("7-Day Moving Averages", new Color(60, 160, 60));
        regionalBtn = createStyledButton("Regional Comparison", new Color(180, 120, 60));
        
        buttonPanel.add(totalCasesBtn);
        buttonPanel.add(fatalityRateBtn);
        buttonPanel.add(movingAvgBtn);
        buttonPanel.add(regionalBtn);
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        // Results display area
        resultsArea = new JTextArea(12, 50);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultsArea.setBackground(new Color(250, 250, 250));
        
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)), 
            "Analysis Results"
        ));
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void setupEventHandlers() {
        // Connect buttons to their actions
        totalCasesBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTotalCases();
            }
        });
        
        fatalityRateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateFatalityRate();
            }
        });
        
        movingAvgBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMovingAverages();
            }
        });
        
        regionalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegionalComparison();
            }
        });
    }
    
    // === ANALYSIS METHODS ===
    
    private void showTotalCases() {
        StringBuilder result = new StringBuilder();
        result.append("TOTAL COVID-19 CASES ANALYSIS\n");
        result.append("==============================\n\n");
        
        if (covidData.isEmpty()) {
            result.append("No data available. Please check the CSV file.\n");
            resultsArea.setText(result.toString());
            return;
        }
        
        int totalCases = 0;
        int regionA = 0, regionB = 0, regionC = 0;
        
        for (String[] record : covidData) {
            if (record.length >= 6) {
                int cases = Integer.parseInt(record[3].trim());
                totalCases += cases;
                
                if (record[1].equals("RegionA")) regionA += cases;
                else if (record[1].equals("RegionB")) regionB += cases;
                else if (record[1].equals("RegionC")) regionC += cases;
            }
        }
        
        result.append(String.format("Total Cases Across All Regions: %,d\n\n", totalCases));
        result.append("Breakdown by Region:\n");
        result.append(String.format("  Region A: %,d cases (%.1f%%)\n", regionA, (regionA * 100.0 / totalCases)));
        result.append(String.format("  Region B: %,d cases (%.1f%%)\n", regionB, (regionB * 100.0 / totalCases)));
        result.append(String.format("  Region C: %,d cases (%.1f%%)\n", regionC, (regionC * 100.0 / totalCases)));
        
        // Add peak day analysis
        result.append("\n--- PEAK CASE DAYS ---\n");
        String[] peakDays = findPeakDays();
        for (String peakDay : peakDays) {
            result.append(peakDay).append("\n");
        }
        
        resultsArea.setText(result.toString());
    }
    
    private void calculateFatalityRate() {
        StringBuilder result = new StringBuilder();
        result.append("CASE FATALITY RATE ANALYSIS\n");
        result.append("============================\n\n");
        
        if (covidData.isEmpty()) {
            result.append("No data available. Please check the CSV file.\n");
            resultsArea.setText(result.toString());
            return;
        }
        
        int totalCases = 0, totalDeaths = 0;
        int casesA = 0, deathsA = 0, casesB = 0, deathsB = 0, casesC = 0, deathsC = 0;
        
        for (String[] record : covidData) {
            if (record.length >= 6) {
                int cases = Integer.parseInt(record[3].trim());
                int deaths = Integer.parseInt(record[5].trim());
                
                totalCases += cases;
                totalDeaths += deaths;
                
                if (record[1].equals("RegionA")) {
                    casesA += cases;
                    deathsA += deaths;
                } else if (record[1].equals("RegionB")) {
                    casesB += cases;
                    deathsB += deaths;
                } else if (record[1].equals("RegionC")) {
                    casesC += cases;
                    deathsC += deaths;
                }
            }
        }
        
        result.append(String.format("Overall Fatality Rate: %.2f%%\n\n", (totalDeaths * 100.0 / totalCases)));
        result.append("Regional Fatality Rates:\n");
        result.append(String.format("  Region A: %.2f%% (%d deaths / %d cases)\n", 
            (deathsA * 100.0 / casesA), deathsA, casesA));
        result.append(String.format("  Region B: %.2f%% (%d deaths / %d cases)\n", 
            (deathsB * 100.0 / casesB), deathsB, casesB));
        result.append(String.format("  Region C: %.2f%% (%d deaths / %d cases)\n", 
            (deathsC * 100.0 / casesC), deathsC, casesC));
        
        resultsArea.setText(result.toString());
    }
    
    private void showMovingAverages() {
        StringBuilder result = new StringBuilder();
        result.append("7-DAY MOVING AVERAGE ANALYSIS\n");
        result.append("==============================\n\n");
        
        if (covidData.isEmpty()) {
            result.append("No data available. Please check the CSV file.\n");
            resultsArea.setText(result.toString());
            return;
        }
        
        // Calculate moving averages for each region
        result.append("REGION A - 7-Day Moving Averages:\n");
        result.append(calculateMovingAverageForRegion("RegionA"));
        result.append("\nREGION B - 7-Day Moving Averages:\n");
        result.append(calculateMovingAverageForRegion("RegionB"));
        result.append("\nREGION C - 7-Day Moving Averages:\n");
        result.append(calculateMovingAverageForRegion("RegionC"));
        
        resultsArea.setText(result.toString());
    }
    
    private void showRegionalComparison() {
        StringBuilder result = new StringBuilder();
        result.append("REGIONAL COMPARISON ANALYSIS\n");
        result.append("=============================\n\n");
        
        if (covidData.isEmpty()) {
            result.append("No data available. Please check the CSV file.\n");
            resultsArea.setText(result.toString());
            return;
        }
        
        int casesA = 0, recoveriesA = 0, deathsA = 0;
        int casesB = 0, recoveriesB = 0, deathsB = 0;
        int casesC = 0, recoveriesC = 0, deathsC = 0;
        
        for (String[] record : covidData) {
            if (record.length >= 6) {
                int cases = Integer.parseInt(record[3].trim());
                int recoveries = Integer.parseInt(record[4].trim());
                int deaths = Integer.parseInt(record[5].trim());
                
                if (record[1].equals("RegionA")) {
                    casesA += cases;
                    recoveriesA += recoveries;
                    deathsA += deaths;
                } else if (record[1].equals("RegionB")) {
                    casesB += cases;
                    recoveriesB += recoveries;
                    deathsB += deaths;
                } else if (record[1].equals("RegionC")) {
                    casesC += cases;
                    recoveriesC += recoveries;
                    deathsC += deaths;
                }
            }
        }
        
        result.append("COMPREHENSIVE REGIONAL COMPARISON:\n\n");
        
        result.append("=== REGION A ===\n");
        result.append(String.format("Total Cases:     %,10d\n", casesA));
        result.append(String.format("Total Recoveries:%,10d\n", recoveriesA));
        result.append(String.format("Total Deaths:   %,10d\n", deathsA));
        result.append(String.format("Recovery Rate:  %10.1f%%\n", (recoveriesA * 100.0 / casesA)));
        result.append(String.format("Fatality Rate:  %10.1f%%\n\n", (deathsA * 100.0 / casesA)));
        
        result.append("=== REGION B ===\n");
        result.append(String.format("Total Cases:     %,10d\n", casesB));
        result.append(String.format("Total Recoveries:%,10d\n", recoveriesB));
        result.append(String.format("Total Deaths:   %,10d\n", deathsB));
        result.append(String.format("Recovery Rate:  %10.1f%%\n", (recoveriesB * 100.0 / casesB)));
        result.append(String.format("Fatality Rate:  %10.1f%%\n\n", (deathsB * 100.0 / casesB)));
        
        result.append("=== REGION C ===\n");
        result.append(String.format("Total Cases:     %,10d\n", casesC));
        result.append(String.format("Total Recoveries:%,10d\n", recoveriesC));
        result.append(String.format("Total Deaths:   %,10d\n", deathsC));
        result.append(String.format("Recovery Rate:  %10.1f%%\n", (recoveriesC * 100.0 / casesC)));
        result.append(String.format("Fatality Rate:  %10.1f%%\n", (deathsC * 100.0 / casesC)));
        
        resultsArea.setText(result.toString());
    }
    
    // === HELPER METHODS ===
    
    private String calculateMovingAverageForRegion(String region) {
        StringBuilder maResult = new StringBuilder();
        
        // Filter data for the specific region
        List<Integer> regionCases = new ArrayList<>();
        List<String> regionDates = new ArrayList<>();
        
        for (String[] record : covidData) {
            if (record.length >= 6 && record[1].equals(region)) {
                try {
                    regionCases.add(Integer.parseInt(record[3].trim()));
                    regionDates.add(record[2].trim());
                } catch (NumberFormatException e) {
                    // Skip invalid records
                }
            }
        }
        
        // Calculate 7-day moving average
        if (regionCases.size() >= 7) {
            for (int i = 6; i < regionCases.size(); i++) {
                int sum = 0;
                for (int j = i - 6; j <= i; j++) {
                    sum += regionCases.get(j);
                }
                double average = sum / 7.0;
                maResult.append(String.format("  %s: %.1f cases\n", regionDates.get(i), average));
                
                // Only show first 5 averages for readability
                if (i >= 10) break;
            }
        } else {
            maResult.append("  Not enough data for 7-day moving average\n");
        }
        
        return maResult.toString();
    }
    
    private String[] findPeakDays() {
        // Simple peak detection - find days with highest cases for each region
        String[] peaks = new String[3];
        int maxA = 0, maxB = 0, maxC = 0;
        String peakDayA = "", peakDayB = "", peakDayC = "";
        
        for (String[] record : covidData) {
            if (record.length >= 6) {
                int cases = Integer.parseInt(record[3].trim());
                String region = record[1];
                String date = record[2];
                
                if (region.equals("RegionA") && cases > maxA) {
                    maxA = cases;
                    peakDayA = date;
                } else if (region.equals("RegionB") && cases > maxB) {
                    maxB = cases;
                    peakDayB = date;
                } else if (region.equals("RegionC") && cases > maxC) {
                    maxC = cases;
                    peakDayC = date;
                }
            }
        }
        
        peaks[0] = String.format("Region A: %d cases on %s", maxA, peakDayA);
        peaks[1] = String.format("Region B: %d cases on %s", maxB, peakDayB);
        peaks[2] = String.format("Region C: %d cases on %s", maxC, peakDayC);
        
        return peaks;
    }
    
    // Keep the existing panel methods (they were working)
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("COVID-19 Statistics Summary", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        // Calculate and display statistics
        if (!covidData.isEmpty()) {
            StringBuilder stats = new StringBuilder();
            stats.append("=== COVID-19 STATISTICS SUMMARY ===\n\n");
            
            // Basic calculations
            int totalCases = 0, totalRecoveries = 0, totalDeaths = 0;
            int regionACases = 0, regionBCases = 0, regionCCases = 0;
            
            for (String[] record : covidData) {
                if (record.length >= 6) {
                    int cases = Integer.parseInt(record[3].trim());
                    totalCases += cases;
                    totalRecoveries += Integer.parseInt(record[4].trim());
                    totalDeaths += Integer.parseInt(record[5].trim());
                    
                    // Count by region
                    if (record[1].equals("RegionA")) regionACases += cases;
                    else if (record[1].equals("RegionB")) regionBCases += cases;
                    else if (record[1].equals("RegionC")) regionCCases += cases;
                }
            }
            
            stats.append(String.format("Total Cases: %,d\n", totalCases));
            stats.append(String.format("Total Recoveries: %,d\n", totalRecoveries));
            stats.append(String.format("Total Deaths: %,d\n\n", totalDeaths));
            
            stats.append("--- Cases by Region ---\n");
            stats.append(String.format("Region A: %,d cases\n", regionACases));
            stats.append(String.format("Region B: %,d cases\n", regionBCases));
            stats.append(String.format("Region C: %,d cases\n\n", regionCCases));
            
            if (totalCases > 0) {
                double fatalityRate = (double) totalDeaths / totalCases * 100;
                double recoveryRate = (double) totalRecoveries / totalCases * 100;
                stats.append(String.format("Case Fatality Rate: %.2f%%\n", fatalityRate));
                stats.append(String.format("Recovery Rate: %.2f%%\n", recoveryRate));
            }
            
            statsArea.setText(stats.toString());
        } else {
            statsArea.setText("No data available. Please check the CSV file.");
        }
        
        JScrollPane scrollPane = new JScrollPane(statsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDataPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("COVID-19 Raw Data View", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        
        // Create table data
        String[] columnNames = {"Daily ID", "Region", "Date", "New Cases", "Recoveries", "Deaths"};
        String[][] tableData = new String[Math.min(20, covidData.size())][6];
        
        for (int i = 0; i < Math.min(20, covidData.size()); i++) {
            String[] record = covidData.get(i);
            for (int j = 0; j < Math.min(record.length, 6); j++) {
                tableData[i][j] = record[j];
            }
        }
        
        dataTable = new JTable(tableData, columnNames);
        dataTable.setFont(new Font("Arial", Font.PLAIN, 12));
        dataTable.setRowHeight(25);
        
        JScrollPane tableScrollPane = new JScrollPane(dataTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("Advanced Analysis Features", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        
        JTextArea analysisArea = new JTextArea();
        analysisArea.setEditable(false);
        analysisArea.setFont(new Font("Arial", Font.PLAIN, 14));
        
        analysisArea.setText(
            "ADVANCED ANALYSIS FEATURES\n\n" +
            "Available Analysis Types:\n" +
            "- Regional trend analysis\n" +
            "- 7-day moving averages\n" +
            "- Case fatality rate by region\n" +
            "- Recovery efficiency analysis\n" +
            "- Outbreak detection algorithms\n" +
            "- Predictive modeling\n" +
            "- Comparative regional charts\n\n" +
            
            "Implementation Status:\n" +
            "✓ Basic statistics calculated\n" +
            "✓ Data parsing and validation\n" +
            "✓ Regional comparisons\n" +
            "○ Charts and graphs (next phase)\n" +
            "○ Predictive analytics (future)\n" +
            "○ Export functionality (future)"
        );
        
        JScrollPane scrollPane = new JScrollPane(analysisArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            CovidAnalysisGUI gui = new CovidAnalysisGUI();
            gui.setVisible(true);
            System.out.println("Enhanced COVID-19 Analysis GUI launched successfully!");
        });
    }
}