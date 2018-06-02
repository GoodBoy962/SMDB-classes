package com.pliskin;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BulkUpdater extends BaseJdbc {

    private static final String query = "INSERT INTO SIM (NAME) VALUES (?)";
    private static final String up_query = "UPDATE SIM SET NAME = ? WHERE ID = ?";
    private static final String del_query = "DELETE FROM sim WHERE id < 1000000";

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        setUpJDBC();
//        PreparedStatement statement = connection.prepareStatement(query);
//        for (int i = 0; i < 5000000; i++) {
//            statement.setString(1, String.valueOf(i * 10 + i - 1));
//            statement.addBatch();
//        }
//        statement.executeBatch();
//        PreparedStatement statement = connection.prepareStatement(up_query);
//        for (int i = 1; i < 4000000; i++) {
//            statement.setString(1, String.valueOf(i * 1000 + i - 1));
//            statement.setLong(2, i);
//            statement.addBatch();
//        }
        PreparedStatement statement = connection.prepareStatement(query);
        for (int i = 0; i < 50; i++) {
            statement.setString(1, String.valueOf(i * 10 + i -1));
            statement.addBatch();
        }
        statement.executeBatch();
//        PreparedStatement statement = connection.prepareStatement(del_query);
    }

}
