package com.entingwu.restfulwebservicesclient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataReader {
    
    private static final String DATA_READER = DataReader.class.getName();
    private static final String FILE_NAME = 
            "/Users/entingwu/NetBeansProjects/RestfulWebServicesClient/"
            + "src/main/resources/BSDSAssignment2Day1.csv";
    
    public static void readFile(List<Record> records) {
        String line;
        BufferedReader br = null;
        int i = 0;
        try {
            br = new BufferedReader(new FileReader(FILE_NAME));
            while ((line = br.readLine()) != null) {
                if (i > 0 && i < 6) {
                    String[] strs = line.split(",");
                    Record record = new Record(
                            strs[0], strs[1], strs[2], 
                            Integer.parseInt(strs[3]), strs[4]);
                    records.add(record); 
                }
                i++;
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(DATA_READER).log(Level.SEVERE, null, ex);
        }
    }
}