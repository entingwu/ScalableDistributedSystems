package com.entingwu.jersey.jdbc;

import com.entingwu.jersey.model.Record;

public class TestJDBC {
    
    public static void main(String[] args) {
        RecordDAO dao = RecordDAO.getRecordDAO();
        dao.cleanUp();
        Record record = new Record("0", "1", "8", 30, "1");
        long id = dao.insert(record);
        System.out.println("id: " + id);
        dao.getRecord();
    }
}
