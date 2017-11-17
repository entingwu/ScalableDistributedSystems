package com.entingwu.logservice.jdbc;

import com.entingwu.logservice.cache.LogData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogDAO {
    
    private static final String LOGDAO = LogDAO.class.getName();
    private static final String LOG_DATA = "logdata";
    private static final String INSERT_STMT = 
            "INSERT INTO " + LOG_DATA + "(response_time, error_num) VALUES (?, ?);";
    private static LogDAO instance;
    protected ConnectUtils connectUtils;
   
    protected LogDAO() {
        connectUtils = new ConnectUtils();
    } 
   
    public static LogDAO getInstance() {
        if (instance == null) {
            instance = new LogDAO();
        }
        return instance;
    }
    
    public void getPercentContInDB(String columnName, double percent) {
        Connection connection = null;
        PreparedStatement selectStmt;
        ResultSet result = null;
        String stmt = 
                "SELECT PERCENTILE_CONT(" + percent + ") WITHIN GROUP(ORDER by " 
                + columnName + ") FROM " + LOG_DATA + ";";
        try {
            connection = ConnectUtils.getConnection();
            selectStmt = connection.prepareStatement(stmt);
            result = selectStmt.executeQuery();
            if (result.next()) {
            }
            selectStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(LOGDAO).log(Level.SEVERE, null, ex);
        }
    }
    
    public void batchInsertLogDAO(List<LogData> logDataList) throws SQLException {
        Connection connection = null;
        PreparedStatement insertStmt = null;
        LogData log = null;
        try {
            connection = ConnectUtils.getConnection();
            insertStmt = connection.prepareStatement(INSERT_STMT);
            synchronized(logDataList) {
                Iterator iter = logDataList.iterator();
                while (iter.hasNext()) {
                    log = (LogData)iter.next();
                    insertStmt.setInt(1, log.getResponseTime());
                    insertStmt.setInt(2, log.getErrorNum());
                    insertStmt.addBatch();
                }
            }
            insertStmt.executeBatch();
            insertStmt.close();
        } catch(SQLException ex) {
            java.util.logging.Logger.getLogger(LOGDAO).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
