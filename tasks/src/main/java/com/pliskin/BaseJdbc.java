package com.pliskin;

import java.sql.*;

public class BaseJdbc {

    protected static final String DRIVER_NAME = "org.postgresql.Driver";
    protected static final String JDBC_URL = "jdbc:postgresql://localhost:5432/alex";
    protected static final String JDBC_PASSWORD = "";
    protected static final String JDBC_LOGIN = "alex";

    protected static Connection connection;
    protected static Statement statement;
    protected static ResultSet resultSet;

    protected static void setUpJDBC() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER_NAME);
        connection = DriverManager.getConnection(JDBC_URL, JDBC_LOGIN, JDBC_PASSWORD);
    }

}
