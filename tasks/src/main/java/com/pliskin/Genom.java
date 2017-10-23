package com.pliskin;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.*;

public class Genom extends BaseJdbc {

    private static final String GENOMS_LOCATION = "src/main/resources/genoms/";

    public static void main(String[] args) throws Exception {
        setUpJDBC();
        uploadShinglesGenoms(9);
        connection.close();
    }

    private static void uploadShinglesGenoms(int pLength) throws FileNotFoundException {
        for (int i = 1; i <= 2; i++) {
            String fileName = "Genome_" + i + ".txt";
            Scanner sc = new Scanner(new File(GENOMS_LOCATION + fileName));
            StringBuilder sb = new StringBuilder();
            while (sc.hasNext()) {
                sb.append(sc.next());
            }
            String genom = sb.toString();
            Set<String> shingles = new HashSet<>();
            for (int j = 0; j < genom.length() - pLength - 1; j++) {
                String shingle = genom.substring(j, j + pLength);
                shingles.add(shingle);
            }
            final int fileId = i;
            shingles.forEach(shingle -> addNewShingle(shingle, fileId, pLength));
        }
    }

    private static void addNewShingle(String shingle, int fileId, int length) {
        try {
            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO test.shingles_" + length + " (shingle, genom) VALUES ('" + shingle + "', " + fileId + ")");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
