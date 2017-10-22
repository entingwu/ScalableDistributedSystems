package com.entingwu.jersey.jdbc;

import com.entingwu.jersey.model.Record;
import com.entingwu.jersey.model.SkiMetric;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;

@Singleton
public class SkiMetricDAO {
    
    private static final String SKI_METRIC_DAO = SkiMetricDAO.class.getName();
    private static final String SKI_METRIC_TABLE = "skimetrics";
    private static SkiMetricDAO instance = null;
    protected ConnectUtils connectionUtils;
    
    protected SkiMetricDAO() {
        connectionUtils = new ConnectUtils();
    }
    
    public static SkiMetricDAO getSkiMetricDAO() {
        if (instance == null) {
            instance = new SkiMetricDAO();
        }
        return instance;
    }
    
    // get
    public SkiMetric findSkiMetricByFilter(String skierID, String dayNum) 
            throws SQLException {
        String stmt = "SELECT * FROM " + SKI_METRIC_TABLE + " WHERE skier_id = ? and day_num = ?";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        SkiMetric skiMetric = null;
        
        try {
            connection = ConnectUtils.getConnection();
            selectStmt = connection.prepareStatement(stmt);
            selectStmt.setString(1, skierID);
            selectStmt.setString(2, dayNum);
            results = selectStmt.executeQuery();
            while (results.next()) {
                skiMetric = new SkiMetric(
                        results.getString(1), 
                        results.getString(2), 
                        results.getString(3), 
                        results.getInt(4), 
                        results.getInt(5));
            }
            selectStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(SKI_METRIC_DAO).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return skiMetric;
    }
    
    //update
    public void upsertSkiMetric(Record record) {
        String stmt = "INSERT INTO skimetrics(id, skier_id, day_num, "
                + "total_vertical, lift_num) VALUES(?,?,?,?,?) " + 
                "ON CONFLICT (id) DO UPDATE SET " +
                "total_vertical = skimetrics.total_vertical + EXCLUDED.total_vertical, " +
                "lift_num = skimetrics.lift_num + EXCLUDED.lift_num";
        Connection connection = null;
        PreparedStatement insertStmt = null;
        long id = 0;
        
        try {
            connection = ConnectUtils.getConnection();
            insertStmt = connection.prepareStatement(stmt);
            insertStmt.setString(1, record.getSkierID());
            insertStmt.setString(2, record.getDayNum());
            insertStmt.setInt(3, record.getVertical());
            insertStmt.setInt(4, 1);
            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(SKI_METRIC_DAO).log(Level.SEVERE, null, ex);
                }
            }
            insertStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(SKI_METRIC_DAO).log(Level.SEVERE, null, ex);
        }  
    }
    
    // post
    public long findSkiMetric(Record record) throws SQLException {
        String stmt = "SELECT * FROM " + SKI_METRIC_TABLE + 
        " WHERE skier_id = ? and day_num = ?";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        long id = 0;
        try {
            connection = ConnectUtils.getConnection();
            selectStmt = connection.prepareStatement(stmt);
            selectStmt.setString(1, record.getSkierID());
            selectStmt.setString(2, record.getDayNum());
            results = selectStmt.executeQuery();
            if (results != null) {
                SkiMetric skiRecord = null;
                while (results.next()) {
                    skiRecord = new SkiMetric(
                            results.getString(1), 
                            results.getString(2), 
                            results.getString(3), 
                            results.getInt(4), 
                            results.getInt(5));
                }
                //updateSkiMetric(connection, record, skiRecord);
                insertSkiMetric(connection, record);
            } else {
                insertSkiMetric(connection, record);
            }
            selectStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(SKI_METRIC_DAO).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return id;
    }

    public long updateSkiMetric(Connection connection, Record record, SkiMetric skiRecord) {
        String stmt = "UPDATE " + SKI_METRIC_TABLE + 
        " SET total_vert = ?, " + " SET lift_num = ?" + 
        " WHERE skier_id = ? AND day_num = ?";
        PreparedStatement updateStmt = null;
        long id = 0;
        
        try {
            updateStmt = connection.prepareStatement(stmt);
            updateStmt.setInt(1, skiRecord.getTotalVertical() + record.getVertical());
            updateStmt.setInt(2, skiRecord.getLiftNum() + 1);
            updateStmt.setString(3, record.getSkierID());
            updateStmt.setString(4, record.getDayNum());
            int affectedRows = updateStmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = updateStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(SKI_METRIC_DAO).log(Level.SEVERE, null, ex);
                }
            }
            updateStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(SKI_METRIC_DAO).log(Level.SEVERE, null, ex);
        }
        return id;        
    }

    public long insertSkiMetric(Connection connection, Record record) {
        String stmt = "INSERT INTO " + SKI_METRIC_TABLE + 
        "(skier_id, day_num, total_vertical, lift_num)  " +
        "VALUES (?, ?, ?, ?);";
        PreparedStatement insertStmt = null;
        long id = 0;
        
        try {
            insertStmt = connection.prepareStatement(stmt);
            insertStmt.setString(1, record.getSkierID());
            insertStmt.setString(2, record.getDayNum());
            insertStmt.setInt(3, record.getVertical());
            insertStmt.setInt(4, 1);
            int affectedRows = insertStmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(SKI_METRIC_DAO).log(Level.SEVERE, null, ex);
                }
            }
            insertStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(SKI_METRIC_DAO).log(Level.SEVERE, null, ex);
        }
        return id;        
    }
    
    public void cleanUp() {
        String deleteStmt = "DELETE FROM TABLE " + SKI_METRIC_TABLE;
        Connection connection = null;
        PreparedStatement prepareStmt = null;
        
        try {
            connection = ConnectUtils.getConnection();
            prepareStmt = connection.prepareStatement(deleteStmt);
            prepareStmt.executeUpdate();
            prepareStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(SKI_METRIC_DAO).log(Level.SEVERE, null, ex);
        }
    }
}

