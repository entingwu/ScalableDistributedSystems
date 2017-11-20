package com.entingwu.logservice.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class DbQueryTimeDAO {
    
    private static final String POST_DB_QUERY_DAO = DbQueryTimeDAO.class.getName();
    private static final String POST_DB_QUERY = "postdbquery";
    private static final String INSERT_STMT = 
            "INSERT INTO " + POST_DB_QUERY + "(db_query_time) VALUES (?);";
    private static DbQueryTimeDAO instance;
    protected ConnectUtils connectUtils;
   
    protected DbQueryTimeDAO() {
        connectUtils = new ConnectUtils();
    } 
   
    public static DbQueryTimeDAO getInstance() {
        if (instance == null) {
            instance = new DbQueryTimeDAO();
        }
        return instance;
    }
    
    public void batchInsertPostLogDAO(List<Integer> dbQueryTimeList) 
            throws SQLException {
        Connection connection = null;
        PreparedStatement insertStmt = null;
        try {
            connection = ConnectUtils.getConnection();
            insertStmt = connection.prepareStatement(INSERT_STMT);
            synchronized(dbQueryTimeList) {
                Iterator iter = dbQueryTimeList.iterator();
                while (iter.hasNext()) {
                    insertStmt.setInt(1, (Integer)iter.next());
                    insertStmt.addBatch();
                }
            }
            insertStmt.executeBatch();
            insertStmt.close();
        } catch(SQLException ex) {
            java.util.logging.Logger.getLogger(POST_DB_QUERY_DAO).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
