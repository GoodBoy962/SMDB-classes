package com.pliskin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Genom extends BaseJdbc {

    private static final String GENOMS_LOCATION = "src/main/resources/genoms/";

    public static void main(String[] args) throws Exception {
        setUpJDBC();
        parseGenoms(12);
        connection.close();
    }

    private static void parseGenoms(int pLength) throws FileNotFoundException {
        for (int i = 1; i <= 2; i++) {
            String fileName = "Genome_" + i + ".txt";
            Scanner sc = new Scanner(new File(GENOMS_LOCATION + fileName));
            StringBuilder sb = new StringBuilder();
            while (sc.hasNext()) {
                sb.append(sc.next());
            }
            String genom = sb.toString();
            Set<String> ps = new HashSet<>();
            for (int j = 0; j < genom.length() - pLength - 1; j++) {
                String p = genom.substring(j, j + pLength);
                ps.add(p);
            }
            System.out.println(ps.size());
        }
    }

}
