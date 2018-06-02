package com.pliskin;

import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class SimpletsAudit extends BaseJdbc {

    private static final String SQL_GET_AUDIT_FALSE =
            "SELECT * FROM AUDIT WHERE AUDIT.SUCCESS = FALSE";

    private static final String SQL_INSERT_TO_DUPLICATE =
            "INSERT INTO DUPLICATE (ID, VALUE) VALUES (?, ?)";

    private static final String SQL_DELETE_IN_DUPLICATE =
            "DELETE FROM DUPLICATE WHERE ID = ?";

    private static final String SQL_UPDATE_IN_DUPLICATE =
            "UPDATE DUPLICATE SET VALUE = ? WHERE ID = ?";

    private static final String SQL_GET_SIMPLETS_BY_ID =
            "SELECT VALUE FROM SIMPLETS WHERE ID = ?";

    private static final String SQL_SET_AUDIT_TRUE =
            "UPDATE AUDIT SET SUCCESS = TRUE " +
                    "WHERE ORIGINAL_ID = (?) " +
                    "AND TABLE_NAME = (?) " +
                    "AND OPERATION = (?) " +
//                    "AND TIME = (?) " +
                    "AND SUCCESS = FALSE";

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        setUpJDBC();
        Statement statement = connection.createStatement();
        PreparedStatement insertStatement = connection.prepareStatement(SQL_INSERT_TO_DUPLICATE);
        PreparedStatement updateStatement = connection.prepareStatement(SQL_UPDATE_IN_DUPLICATE);
        PreparedStatement updateAuditStatement = connection.prepareStatement(SQL_SET_AUDIT_TRUE);
        PreparedStatement deleteStatement = connection.prepareStatement(SQL_DELETE_IN_DUPLICATE);
        PreparedStatement getSimpletById = connection.prepareStatement(SQL_GET_SIMPLETS_BY_ID);
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                ResultSet rs;
                try {
                    rs = statement.executeQuery(SQL_GET_AUDIT_FALSE);
                    while (rs.next()) {
                        Integer id = rs.getInt("ORIGINAL_ID");
                        String tableName = rs.getString("TABLE_NAME");
                        String operation = rs.getString("OPERATION");
                        Date time = rs.getDate("TIME");

                        switch (operation) {
                            case "DELETE":
                                deleteStatement.setInt(1, id);
                                deleteStatement.addBatch();
                                break;
                            case "INSERT":
                                getSimpletById.setInt(1, id);
                                ResultSet resultSet = getSimpletById.executeQuery();
                                while (resultSet.next()) {
                                    String value = resultSet.getString("VALUE");
                                    insertStatement.setInt(1, id);
                                    insertStatement.setString(2, value);
                                    insertStatement.addBatch();
                                }
                                break;
                            case "UPDATE":
                                getSimpletById.setInt(1, id);
                                ResultSet resultSet1 = getSimpletById.executeQuery();
                                while (resultSet1.next()) {
                                    String value1 = resultSet1.getString("VALUE");
                                    updateStatement.setString(1, value1);
                                    updateStatement.addBatch();
                                }
                                break;
                        }

                        //update to true
                        updateAuditStatement.setInt(1, id);
                        updateAuditStatement.setString(2, tableName);
                        updateAuditStatement.setString(3, operation);
                        //updateAuditStatement.setDate(4, time);
                        updateAuditStatement.addBatch();
                    }

                    insertStatement.executeBatch();
                    updateAuditStatement.executeBatch();
                    deleteStatement.executeBatch();
                    updateAuditStatement.executeBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 10000);
    }

}
