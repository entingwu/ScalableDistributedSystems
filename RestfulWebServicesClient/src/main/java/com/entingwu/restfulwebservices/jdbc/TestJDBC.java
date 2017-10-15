package com.entingwu.restfulwebservices.jdbc;

import java.sql.Connection;

public class TestJDBC {
    
    public static void main(String[] args) {
        Connection connection = ConnectionUtils.getConnection();
    }
}
