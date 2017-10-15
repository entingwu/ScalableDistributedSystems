package com.entingwu.restfulwebservicesclient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataReader {
    
    private static final String FILE_NAME = 
            "/Users/entingwu/NetBeansProjects/RestfulWebServicesClient/"
            + "src/main/resources/BSDSAssignment2Day1.csv";
    
    public static void readFile(ConcurrentLinkedQueue<Record> queue) {
        String line;
        BufferedReader br = null;
        int i = 0;
        try {
            br = new BufferedReader(new FileReader(FILE_NAME));
            while ((line = br.readLine()) != null) {
                if (i > 0 && i < 30) {
                    String[] strs = line.split(",");
                    String resortID = strs[0];
                    int dayNum = Integer.parseInt(strs[1]);
                    String skierId = strs[2];
                    String liftId = strs[3];
                    String timestamp = strs[4];
                    Record record = new Record(
                            resortID, dayNum, skierId, liftId, timestamp);
                    queue.offer(record);                  
                }
                i++;
            }
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(DataReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
