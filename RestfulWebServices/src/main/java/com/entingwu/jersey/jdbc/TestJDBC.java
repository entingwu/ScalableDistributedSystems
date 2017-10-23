package com.entingwu.jersey.jdbc;

import com.entingwu.jersey.model.RFIDLiftData;
import java.sql.SQLException;

public class TestJDBC {
    
    public static void main(String[] args) throws SQLException {
        RFIDLiftDAO dao = RFIDLiftDAO.getRFIDLiftDAO();
        dao.cleanUp();
        RFIDLiftData record = new RFIDLiftData("0", "1", "8", 30, "1");
        long id = dao.insertRFIDLift(record);
        System.out.println("id: " + id);
    }
}
