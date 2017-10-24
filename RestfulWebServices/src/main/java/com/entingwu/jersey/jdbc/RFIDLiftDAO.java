package com.entingwu.jersey.jdbc;

import com.entingwu.jersey.model.RFIDLiftData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;

@Singleton
public class RFIDLiftDAO {
    
    private static final String RFIDLiftDAO = RFIDLiftDAO.class.getName();
    private static final String SKI_DATA = "skidata";
    private static RFIDLiftDAO instance = null;
    protected ConnectUtils connectionUtils;
    
    protected RFIDLiftDAO() {
        connectionUtils = new ConnectUtils();
    }
    
    public static RFIDLiftDAO getRFIDLiftDAO() {
        if (instance == null) {
            instance = new RFIDLiftDAO();
        }
        return instance;
    }
    
    public List<RFIDLiftData> batchInsertRFIDLift(List<RFIDLiftData> liftDataList) 
            throws SQLException {
        System.out.println("batchInsertRFIDLift here: " + liftDataList.size());
        String stmt = "INSERT INTO " + SKI_DATA + 
        "(resort_id, day_num, skier_id, lift_id, timestamp)  " +
        "VALUES (?, ?, ?, ?, ?);";
        System.out.println("batchInsertRFIDLift " + stmt);
        Connection connection = null;
        PreparedStatement insertStmt = null;
        List<RFIDLiftData> failedList = new ArrayList<>();
        
        try {
            connection = ConnectUtils.getConnection();
            insertStmt = connection.prepareStatement(stmt);
            for (RFIDLiftData record : liftDataList) {
                insertStmt.setString(1, record.getResortID());
                insertStmt.setString(2, record.getDayNum());
                insertStmt.setString(3, record.getSkierID());
                insertStmt.setInt(4, record.getLiftID());
                insertStmt.setString(5, record.getTime());
                insertStmt.addBatch();
            }
            int[] results = insertStmt.executeBatch();
            for (int i = 0; i < results.length; i++) {
                if (results[i] == Statement.EXECUTE_FAILED) {
                    failedList.add(liftDataList.get(i));
                }
            }
            insertStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RFIDLiftDAO).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return failedList;        
    }
    
    public long insertRFIDLift(RFIDLiftData record) throws SQLException {
        String stmt = "INSERT INTO " + SKI_DATA + 
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
                    Logger.getLogger(RFIDLiftDAO).log(Level.SEVERE, null, ex);
                }
            }
            insertStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RFIDLiftDAO).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return id;        
    }
    
    public void cleanUp() {
        String deleteStmt = "DELETE FROM TABLE " + SKI_DATA;
        Connection connection = null;
        PreparedStatement prepareStmt = null;
        
        try {
            connection = ConnectUtils.getConnection();
            prepareStmt = connection.prepareStatement(deleteStmt);
            prepareStmt.executeUpdate();
            prepareStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RFIDLiftDAO).log(Level.SEVERE, null, ex);
        }
    }
}
