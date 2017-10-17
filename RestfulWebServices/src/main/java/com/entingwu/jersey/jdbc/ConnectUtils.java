package com.entingwu.jersey.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectUtils {
    
    private static final String CONNECTION_MANAGER = ConnectUtils.class.getName();
    private static final String REMOTE_URL = "jdbc:postgresql://postgresqldb."
            + "cmsolnnzjn16.us-west-2.rds.amazonaws.com:5432/postgresql";
    private static final String LOCAL_URL = "jdbc:postgresql://localhost:5432/postgresql";
    private static final String USER = "entingwu" ;
    private static final String PASSWORD = "entingwu1221" ;
    
    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            Logger.getLogger(CONNECTION_MANAGER).log(Level.SEVERE, null, e);
        }
        
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(REMOTE_URL, USER, PASSWORD);
        } catch (SQLException ex) {
            Logger.getLogger(CONNECTION_MANAGER).log(Level.SEVERE, null, ex);
        }
        return connection;
    }
    
    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(CONNECTION_MANAGER).log(Level.SEVERE, null, ex);
        }
    }
}
