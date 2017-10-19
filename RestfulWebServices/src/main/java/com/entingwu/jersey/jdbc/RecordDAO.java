package com.entingwu.jersey.jdbc;

import com.entingwu.jersey.model.Record;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;

@Singleton
public class RecordDAO {
    
    private static final String RECORD_DAO = RecordDAO.class.getName();
    private static final String TABLE = "skidata";
    private static RecordDAO instance = null;
    protected ConnectUtils connectionUtils;
    
    protected RecordDAO() {
        connectionUtils = new ConnectUtils();
    }
    
    public static RecordDAO getRecordDAO() {
        if (instance == null) {
            instance = new RecordDAO();
        }
        return instance;
    }
    
    public Record findRecordByFilter(String skierID, String dayNum) {
        String stmt = "SELECT * FROM " + TABLE + " WHERE skier_id = ? and day_num = ?";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        Record record = null;
        
        try {
            connection = ConnectUtils.getConnection();
            selectStmt = connection.prepareStatement(stmt);
            selectStmt.setString(1, skierID);
            selectStmt.setString(2, dayNum);
            results = selectStmt.executeQuery();
            while (results.next()) {
                record = new Record(
                        results.getString(1), 
                        results.getString(2), 
                        results.getString(3), 
                        results.getString(4), 
                        results.getInt(5), 
                        results.getString(6));
            }
            selectStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RECORD_DAO).log(Level.SEVERE, null, ex);
        }
        return record;
    }
    
    public void getRecord() {
        String stmt = "SELECT * FROM " + TABLE;
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        
        try {
            connection = ConnectUtils.getConnection();
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
            Logger.getLogger(RECORD_DAO).log(Level.SEVERE, null, ex);
        }
    }
    
    public long insert(Record record) {
        String stmt = "INSERT INTO " + TABLE + 
        "(resort_id, day_num, skier_id, lift_id, timestamp)  " +
        "VALUES (?, ?, ?, ?, ?);";
        Connection connection = null;
        PreparedStatement insertStmt = null;
        long id = 0;
        
        try {
            connection = ConnectUtils.getConnection();
            insertStmt = connection.prepareStatement(stmt);
            insertStmt.setString(1, record.getResortID());
            insertStmt.setString(2, record.getDayNum());
            insertStmt.setString(3, record.getSkierID());
            insertStmt.setInt(4, record.getLiftID());
            insertStmt.setString(5, record.getTime());
            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(RECORD_DAO).log(Level.SEVERE, null, ex);
                }
            }
            insertStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RECORD_DAO).log(Level.SEVERE, null, ex);
        }
        return id;        
    }
    
    public void cleanUp() {
        String deleteStmt = "DELETE FROM TABLE " + TABLE;
        Connection connection = null;
        PreparedStatement prepareStmt = null;
        
        try {
            connection = ConnectUtils.getConnection();
            prepareStmt = connection.prepareStatement(deleteStmt);
            prepareStmt.executeUpdate();
            prepareStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RECORD_DAO).log(Level.SEVERE, null, ex);
        }
    }
}
