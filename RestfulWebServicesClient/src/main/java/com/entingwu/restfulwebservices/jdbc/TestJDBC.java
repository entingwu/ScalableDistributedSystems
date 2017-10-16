package com.entingwu.restfulwebservices.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestJDBC {
    
    public static void main(String[] args) {
        Connection connection = ConnectionUtils.getConnection();
        getRecord(connection);
    }
    
    private static void getRecord(Connection connection) {
        String stmt = "SELECT * FROM skidata";
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
        } catch (SQLException ex) {
            Logger.getLogger(TestJDBC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
