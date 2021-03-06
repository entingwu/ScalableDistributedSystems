package com.entingwu.jersey.jdbc;

import com.entingwu.jersey.model.RFIDLiftData;
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
public class RFIDLiftDAO {
    
    private static final String RFIDLiftDAO = RFIDLiftDAO.class.getName();
    private static final String SKI_DATA = "skidata";
    private static final String INSERT_STMT = 
            "INSERT INTO " + SKI_DATA + "(resort_id, day_num, skier_id, "
            + "lift_id, timestamp) VALUES (?, ?, ?, ?, ?);";
    private static RFIDLiftDAO instance;
    protected ConnectUtils connectionUtils;
    
    protected RFIDLiftDAO() {
        connectionUtils = new ConnectUtils();
    }
    
    public static RFIDLiftDAO getInstance() {
        if (instance == null) {
            instance = new RFIDLiftDAO();
        }
        return instance;
    }
    
    public void batchInsertRFIDLift(List<RFIDLiftData> liftDataList) 
            throws SQLException {
        Connection connection = null;
        PreparedStatement insertStmt = null;
        RFIDLiftData record = null;
        
        try {
            connection = ConnectUtils.getConnection();
            insertStmt = connection.prepareStatement(INSERT_STMT);
            synchronized(liftDataList) {
                Iterator iter = liftDataList.iterator();
                while (iter.hasNext()) {
                    record = (RFIDLiftData)iter.next();
                    setStatement(insertStmt, record);                    
                    insertStmt.addBatch();
                }
            }
            insertStmt.executeBatch();
            insertStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(RFIDLiftDAO).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }       
    }
    
    public long insertRFIDLift(RFIDLiftData record) throws SQLException {
        Connection connection = null;
        PreparedStatement insertStmt = null;
        long id = 0;
        
        try {
            connection = ConnectUtils.getConnection();
            insertStmt = connection.prepareStatement(INSERT_STMT);
            setStatement(insertStmt, record);
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
    
    private void setStatement(PreparedStatement insertStmt, RFIDLiftData record) 
            throws SQLException {
        insertStmt.setString(1, record.getResortID());
        insertStmt.setString(2, record.getDayNum());
        insertStmt.setString(3, record.getSkierID());
        insertStmt.setInt(4, record.getLiftID());
        insertStmt.setString(5, record.getTime());
    }
}
