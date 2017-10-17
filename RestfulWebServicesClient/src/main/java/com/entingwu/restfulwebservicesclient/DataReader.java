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
                if (i > 0 && i < 21) {
                    String[] strs = line.split(",");
                    Record record = new Record(
                            strs[0], strs[1], strs[2], strs[3], strs[4]);
                    queue.offer(record);                  
                }
                i++;
            }
            Record r = new Record();
            r.flag = true;
            queue.offer(r);
            System.out.println("Pushed in the eof");
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(DataReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
