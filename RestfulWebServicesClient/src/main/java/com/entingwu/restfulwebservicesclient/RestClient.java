package com.entingwu.restfulwebservicesclient;

import com.entingwu.restfulwebservicesclient.write.RFIDLiftData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RestClient {
    
    protected static final String LOCAL_URI = 
            "http://localhost:9090/RestfulWebServices/rest";
    protected static String ip = "35.167.118.155";
    protected static String port = "8080";
    protected static String REMOTE_URI = getServerAddress(ip, port);
    protected List<RFIDLiftData> dataList = null;

    public void clientProcessing(
            int threadNum, String ip, String port) throws Exception {
        // 1. Read records from .csv
        System.out.println("Client starting ...... Time:" + getDate());
        dataList = new ArrayList<>();
        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                 DataReader.readFile(dataList);
            }
        });
        reader.start();
        reader.join();
    }
    
    protected static void runTasks(List<Callable<Metrics>> tasks, int threadNum) 
            throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        executorService.invokeAll(tasks);
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
    
    protected static String getServerAddress(String ip, String port) {
        return new StringBuilder()
                .append("http://")
                .append(ip)
                .append(":")
                .append(port)
                .append("/RestfulWebServicesn/rest")
                .toString();
    }
    
    protected static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
