package com.entingwu.jersey.jdbc;

import com.entingwu.jersey.model.RFIDLiftData;
import com.entingwu.jersey.model.SkiMetric;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;

@Singleton
public class SkiMetricDAO {
    
    private static final String SKI_METRIC_DAO = SkiMetricDAO.class.getName();
    private static final String SKI_METRIC_TABLE = "skimetrics";
    private static final String UPSERT_STMT = 
            "INSERT INTO " + SKI_METRIC_TABLE + "(id, skier_id, day_num, "
            + "total_vertical, lift_num) VALUES(?,?,?,?,?) "
            + "ON CONFLICT (id) DO UPDATE SET "
            + "total_vertical = " + SKI_METRIC_TABLE + ".total_vertical + EXCLUDED.total_vertical, "
            + "lift_num = " + SKI_METRIC_TABLE+ ".lift_num + EXCLUDED.lift_num";
    private static SkiMetricDAO instance;
    protected ConnectUtils connectionUtils;
    
    protected SkiMetricDAO() {
        connectionUtils = new ConnectUtils();
    }
    
    public static SkiMetricDAO getInstance() {
        if (instance == null) {
            instance = new SkiMetricDAO();
        }
        return instance;
    }
    
    public SkiMetric findSkiMetricByFilter(String skierID, String dayNum) 
            throws SQLException {
        String stmt = "SELECT * FROM " + SKI_METRIC_TABLE + 
                " WHERE skier_id = ? and day_num = ?";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        SkiMetric skiMetric = new SkiMetric();
        
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
    
    public void batchUpsertSkiMetric(List<SkiMetric> skiMetricList) 
            throws SQLException {
        System.out.println("batchUpsertSkiMetric here " + skiMetricList.size());
        Connection connection = null;
        PreparedStatement upsertStmt = null;
        
        try {
            connection = ConnectUtils.getConnection();
            upsertStmt = connection.prepareStatement(UPSERT_STMT);
            synchronized(skiMetricList) {
                Iterator iter = skiMetricList.iterator();
                while (iter.hasNext()) {
                    SkiMetric skiMetric = (SkiMetric)iter.next();
                    upsertStmt.setString(1, skiMetric.getID());
                    upsertStmt.setString(2, skiMetric.getSkierID());
                    upsertStmt.setString(3, skiMetric.getDayNum());
                    upsertStmt.setInt(4, skiMetric.getTotalVertical());
                    upsertStmt.setInt(5, skiMetric.getLiftNum());
                    upsertStmt.addBatch();
                }
            }
            upsertStmt.executeBatch();
            upsertStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(SKI_METRIC_DAO).log(Level.SEVERE, null, ex);
        }  finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    public long upsertSkiMetric(RFIDLiftData record) throws SQLException {
        Connection connection = null;
        PreparedStatement upsertStmt = null;
        long id = 0;
        
        try {
            connection = ConnectUtils.getConnection();
            upsertStmt = connection.prepareStatement(UPSERT_STMT);
            upsertStmt.setString(1, record.getID());
            upsertStmt.setString(2, record.getSkierID());
            upsertStmt.setString(3, record.getDayNum());
            upsertStmt.setInt(4, record.getVertical());
            upsertStmt.setInt(5, 1);
            int affectedRows = upsertStmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = upsertStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(SKI_METRIC_DAO).log(Level.SEVERE, null, ex);
                }
            }
            upsertStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(SKI_METRIC_DAO).log(Level.SEVERE, null, ex);
        }  finally {
            if (connection != null) {
                connection.close();
            }
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