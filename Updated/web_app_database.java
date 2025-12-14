package co.za.Main.WebTradeApplication;

import java.math.BigDecimal;
import java.sql.*;
import java.io.File;

public class WebAppDataBase implements AutoCloseable {

    private Connection connection;
    private String dataBaseName = "WebAppDataBase.db";
    private String tableName = "WebAppDataBase";
    private String FILENAME = "WebAppDataBase";

    public WebAppDataBase() throws SQLException {
        this(false);
    }

    public WebAppDataBase(boolean resetOnStartup) throws SQLException {
        if (resetOnStartup) {
            deleteExistingFiles();
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + dataBaseName);
        createTable();
    }

    public void deleteExistingFiles() {
        try {
            File dbFile = new File(dataBaseName);
            File sqlFile = new File(FILENAME + ".sql");
            
            if (dbFile.exists()) {
                boolean deleted = dbFile.delete();
                System.out.println("Database file " + (deleted ? "deleted" : "delete failed"));
            }
            
            if (sqlFile.exists()) {
                boolean deleted = sqlFile.delete();
                System.out.println("SQL file " + (deleted ? "deleted" : "delete failed"));
            }
        } catch (Exception e) {
            System.err.println("Error deleting files: " + e.getMessage());
        }
    }

    public void createTable() throws SQLException {
        String sql = String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                variable VARCHAR(50) DEFAULT '0',
                maximum DECIMAL(20,8) DEFAULT 0,
                minimum DECIMAL(20,8) DEFAULT 0,
                returnmin DECIMAL(20,8) DEFAULT 0,
                returnmax DECIMAL(20,8) DEFAULT 0
            )
            """, tableName);
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            populateTableIfEmpty();
        }
    }

    private void populateTableIfEmpty() throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Table is empty - populating with zero values...");
                populateWithZeros();
            } else {
                System.out.println("Table already contains data - checking if migration needed...");
                // Check if old variable names exist and migrate if needed
                migrateOldVariableNamesIfNeeded();
            }
        }
    }

    /**
     * Migrate old variable names (buyvariable, sellvariable) to new names (openingvalue, closingvalue)
     */
    private void migrateOldVariableNamesIfNeeded() throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT variable FROM " + tableName)) {
            
            boolean hasBuyVariable = false;
            boolean hasSellVariable = false;
            boolean hasOpeningValue = false;
            boolean hasClosingValue = false;
            
            while (rs.next()) {
                String var = rs.getString("variable");
                if ("buyvariable".equals(var)) hasBuyVariable = true;
                if ("sellvariable".equals(var)) hasSellVariable = true;
                if ("openingvalue".equals(var)) hasOpeningValue = true;
                if ("closingvalue".equals(var)) hasClosingValue = true;
            }
            
            // If old names exist but new names don't, perform migration
            if ((hasBuyVariable || hasSellVariable) && (!hasOpeningValue || !hasClosingValue)) {
                System.out.println("=== MIGRATION DETECTED ===");
                System.out.println("Migrating from old variable names to new variable names...");
                
                if (hasSellVariable && !hasOpeningValue) {
                    stmt.executeUpdate("UPDATE " + tableName + " SET variable = 'openingvalue' WHERE variable = 'sellvariable'");
                    System.out.println("✅ Migrated sellvariable → openingvalue");
                }
                
                if (hasBuyVariable && !hasClosingValue) {
                    stmt.executeUpdate("UPDATE " + tableName + " SET variable = 'closingvalue' WHERE variable = 'buyvariable'");
                    System.out.println("✅ Migrated buyvariable → closingvalue");
                }
                
                System.out.println("=== MIGRATION COMPLETE ===");
            }
        }
    }

    private void populateWithZeros() throws SQLException {
        // UPDATED: Use openingvalue and closingvalue instead of buyvariable and sellvariable
        String[] variables = {"tradeprofit", "profitfactor", "tradeamount", "openingvalue", "closingvalue"};
        
        String insertSQL = "INSERT INTO " + tableName + 
            " (variable, minimum, maximum, returnmin, returnmax) VALUES (?, 0, 0, 0, 0)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            for (String variable : variables) {
                pstmt.setString(1, variable);
                pstmt.executeUpdate();
                System.out.println("Initialized " + variable + " with all zero values");
            }
        }
    }

    public void resetInputValuesToZero() throws SQLException {
        System.out.println("Resetting input values (min/max) to zero...");
        
        String sql = "UPDATE " + tableName + " SET minimum = 0, maximum = 0";
        try (Statement stmt = connection.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("Reset " + rowsAffected + " input values to zero.");
        }
    }

    public void resetAllValuesToZero() throws SQLException {
        System.out.println("Resetting ALL values to zero...");
        
        String sql = "UPDATE " + tableName + " SET minimum = 0, maximum = 0, returnmin = 0, returnmax = 0";
        try (Statement stmt = connection.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("Reset " + rowsAffected + " rows - all values set to zero.");
        }
    }

    public BigDecimal getValueFromColumn(String variable, String columnName) throws SQLException {
        String sql = String.format("SELECT %s FROM %s WHERE variable = ?", columnName, tableName);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, variable);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                BigDecimal value = rs.getBigDecimal(columnName);
                return value != null ? value : BigDecimal.ZERO;
            } else {
                System.err.println("Variable not found: " + variable + " - returning ZERO");
                return BigDecimal.ZERO;
            }
        }
    }

    public void updateValue(String variable, String columnName, BigDecimal value) throws SQLException {
        String sql = String.format("UPDATE %s SET %s = ? WHERE variable = ?", tableName, columnName);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, value);
            pstmt.setString(2, variable);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No rows updated for variable: " + variable);
            }
            System.out.println("Updated " + variable + "." + columnName + " = " + value.toPlainString());
        }
    }

    public void updateQueryResult(String variable, BigDecimal returnMin, BigDecimal returnMax) throws SQLException {
        String sql = "UPDATE " + tableName + " SET returnmin = ?, returnmax = ? WHERE variable = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, returnMin);
            pstmt.setBigDecimal(2, returnMax);
            pstmt.setString(3, variable);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Updated calculations for " + variable + 
                    " - returnmin: " + returnMin.toPlainString() +
                    ", returnmax: " + returnMax.toPlainString());
            }
        }
    }

    public void refreshInputValues() throws SQLException {
        System.out.println("Refreshing input values from current database state...");
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT variable, minimum, maximum FROM " + tableName)) {
            
            while (rs.next()) {
                String var = rs.getString("variable");
                BigDecimal min = rs.getBigDecimal("minimum");
                BigDecimal max = rs.getBigDecimal("maximum");
                System.out.println("Current " + var + ": min=" + 
                    (min != null ? min.toPlainString() : "NULL") + 
                    ", max=" + (max != null ? max.toPlainString() : "NULL"));
            }
        }
    }

    public void exportToSQL() throws SQLException {
        String fileName =  FILENAME + ".sql";
        
        try (java.io.FileWriter writer = new java.io.FileWriter(fileName);
             java.io.PrintWriter printWriter = new java.io.PrintWriter(writer)) {
            
            printWriter.println("-- " + tableName + " Export");
            printWriter.println("-- Generated on: " + new java.util.Date());
            printWriter.println();
            
            printWriter.println("DROP TABLE IF EXISTS " + tableName + ";");
            printWriter.println();
            printWriter.println("CREATE TABLE " + tableName + " (");
            printWriter.println("    variable VARCHAR(50) DEFAULT '0',");
            printWriter.println("    maximum DECIMAL(20,8) DEFAULT 0,");
            printWriter.println("    minimum DECIMAL(20,8) DEFAULT 0,");
            printWriter.println("    returnmin DECIMAL(20,8) DEFAULT 0,");
            printWriter.println("    returnmax DECIMAL(20,8) DEFAULT 0");
            printWriter.println(");");
            printWriter.println();
            
            String sql = "SELECT * FROM " + tableName;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                printWriter.println("-- Insert data");
                while (rs.next()) {
                    printWriter.printf(
                        "INSERT INTO %s (variable, maximum, minimum, returnmin, returnmax) VALUES ('%s', %s, %s, %s, %s);%n",
                        tableName,
                        rs.getString("variable"),
                        rs.getBigDecimal("maximum").toPlainString(),
                        rs.getBigDecimal("minimum").toPlainString(),
                        rs.getBigDecimal("returnmin").toPlainString(),
                        rs.getBigDecimal("returnmax").toPlainString()
                    );
                }
            }
            
            printWriter.println();
            printWriter.println("-- End of export");
            System.out.println("Database exported to " + fileName);
            
        } catch (java.io.IOException e) {
            throw new SQLException("Error writing to SQL file: " + e.getMessage());
        }
    }

    public void printAllVariables() throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("Trade Variable Database Contents:");
            System.out.println("Variable\t\tMaximum\t\tMinimum\t\tReturnMin\tReturnMax");
            System.out.println("========================================================================");
            
            while (rs.next()) {
                System.out.printf("%-20s\t%s\t%s\t%s\t%s%n",
                    rs.getString("variable"),
                    rs.getBigDecimal("maximum").toPlainString(),
                    rs.getBigDecimal("minimum").toPlainString(),
                    rs.getBigDecimal("returnmin").toPlainString(),
                    rs.getBigDecimal("returnmax").toPlainString()
                );
            }
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}