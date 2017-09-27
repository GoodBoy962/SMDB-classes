package com.pliskin;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final String DRIVER_NAME = "org.postgresql.Driver";
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/db2";
    private static final String JDBC_PASSWORD = "postgres";
    private static final String JDBC_LOGIN = "postgres";
    private static final String DOCS_LOCATION = "src/main/resources/docs/";

    private static final String SQL_SELECT_TF_IDF_BY_WORD_AND_FILE_NAME = "SELECT (SELECT COUNT(*) AS COUNT\n" +
            "        FROM TEST.WORD w INNER JOIN TEST.DOC d ON w.doc_id = d.id\n" +
            "        WHERE w.WORD = ? AND d.NAME = ?) :: DECIMAL / MAX(c.COUNT) :: DECIMAL * log(2.0, (SELECT COUNT(*)\n" +
            "                                                                                                         FROM\n" +
            "                                                                                                           TEST.DOC) :: DECIMAL\n" +
            "                                                                                                        / (SELECT COUNT(\n" +
            "    c.COUNT))) :: DECIMAL AS TF\n" +
            "FROM (SELECT COUNT(*) AS COUNT\n" +
            "      FROM TEST.WORD w\n" +
            "      WHERE w.WORD = ?\n" +
            "      GROUP BY w.DOC_ID) c";

    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, SQLException {
        setUpJDBC();
//        addNewDocuments();
        testTF_IDF();
        connection.close();
    }

    private static void setUpJDBC() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER_NAME);
        connection = DriverManager.getConnection(JDBC_URL, JDBC_LOGIN, JDBC_PASSWORD);
    }

    private static void addNewDocuments() throws FileNotFoundException, SQLException {
        for (int i = 1; i < 6; i++) {
            String fileName = "poem" + i + ".txt";
            Scanner sc = new Scanner(new File(DOCS_LOCATION + fileName));
            sc.useDelimiter("( +)|(\\n)");
            addNewDocument(fileName);
            while (sc.hasNext()) {
                int docId = getDocumentId(fileName);
                String word = sc.next().replaceAll("\\p{P}", "").toUpperCase();
                if (isNotStopWord(word)) {
                    addNewWord(word, docId);
                }
            }
        }
    }

    private static double getWordTF_IDF(String word, String fileName) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SQL_SELECT_TF_IDF_BY_WORD_AND_FILE_NAME);
        statement.setString(1, word);
        statement.setString(2, fileName);
        statement.setString(3, word);
        resultSet = statement.executeQuery();
        resultSet.next();
        double res = resultSet.getDouble(1);
        resultSet.close();
        statement.close();
        return res;
    }

    private static void testTF_IDF() {
        List<String> words = Arrays.asList("YOUR", "I");
        List<String> fileNames = Arrays.asList("poem1.txt", "poem2.txt", "poem3.txt", "poem4.txt", "poem5.txt");
        words.forEach(word -> fileNames.forEach(fileName -> {
            try {
                System.out.printf("The TD.IDF for word : [ %s ] in file [ %s ] is : [ %.4f ]\n", word, fileName, getWordTF_IDF(word, fileName));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    private static void addNewDocument(String fileName) throws SQLException {
        statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO test.doc (name) VALUES ('" + fileName + "')");
        statement.close();
    }

    private static int getDocumentId(String fileName) throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT id FROM test.doc WHERE test.doc.name = '" + fileName + "'");
        resultSet.next();
        int docId = resultSet.getInt("id");
        resultSet.close();
        statement.close();
        return docId;
    }

    private static void addNewWord(String word, int docId) throws SQLException {
        statement = connection.createStatement();
        statement.executeUpdate("INSERT INTO test.word (word, doc_id) VALUES ('" + word + "', " + docId + ")");
        statement.close();
    }

    private static boolean isNotStopWord(String word) throws SQLException {
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT COUNT(*) AS rowcount FROM test.stop_word sw WHERE sw.word = '" + word + "'");
        resultSet.next();
        int count = resultSet.getInt("rowcount");
        resultSet.close();
        statement.close();
        return count == 0;
    }

}