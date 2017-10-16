package com.entingwu.restfulwebservices.jdbc;

import com.entingwu.restfulwebservicesclient.Record;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestJDBC {
    
    private static final String TEST_JDBC = TestJDBC.class.getName();
    private static final String TABLE = "skidata";
    
    private static void getRecord(Connection connection) {
        String stmt = "SELECT * FROM " + TABLE;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        
        try {
            selectStmt = connection.prepareStatement(stmt);
            results = selectStmt.executeQuery();
            while (results.next()) {
                System.out.println(results.getString(1) + "\t" + 
                        results.getString(2) + "\t" + 
                        results.getString(3) + "\t" + 
                        results.getString(4) + "\t" + 
                        results.getString(5) + "\t");
            }
            selectStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TEST_JDBC).log(Level.SEVERE, null, ex);
        }
    }
    
    private static long insertRecord(Connection connection, Record record) {
        String stmt = "INSERT INTO " + TABLE + 
        "(resort_id, day_num, skier_id, lift_id, timestamp)  " +
        "VALUES (?, ?, ?, ?, ?);";
        PreparedStatement insertStmt = null;
        long id = 0;
        
        try {
            insertStmt = connection.prepareStatement(stmt);
            insertStmt.setString(1, record.getResortID());
            insertStmt.setInt(2, record.getDayNum());
            insertStmt.setString(3, record.getSkierID());
            insertStmt.setString(4, record.getLiftID());
            insertStmt.setString(5, record.getTime());
            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(TEST_JDBC).log(Level.SEVERE, null, ex);
                }
            }
            insertStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TEST_JDBC).log(Level.SEVERE, null, ex);
        }
        return id;        
    }
    
    private static void cleanUp(Connection connection) {
        String stmt = "DELETE FROM " + TABLE;
        PreparedStatement deleteStmt = null;
        
        try {
            deleteStmt = connection.prepareStatement(stmt);
            deleteStmt.executeUpdate();
            deleteStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TEST_JDBC).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        Connection connection = ConnectionUtils.getConnection();
        cleanUp(connection);
        Record record = new Record("0", 1, "8", "30", "1");
        long id = insertRecord(connection, record);
        System.out.println("id: " + id);
        getRecord(connection);
    }
}
