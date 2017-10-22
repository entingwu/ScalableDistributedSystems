package com.entingwu.restfulwebservicesclient;

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
    
    private static final String LOCAL_URI = 
            "http://localhost:9090/RestfulWebServices/rest";
    private static String ip = "35.167.118.155";
    private static String port = "8080";
    private static String REMOTE_URI = getServerAddress(ip, port);
    private static int threadNum = 32;
    protected List<Record> recordList = null;

    public void clientProcessing(int threadNum, String ip, String port) 
            throws Exception {
        recordList = new ArrayList<>();
        // 1. Read records from .csv
        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                 DataReader.readFile(recordList);
            }
        });
        reader.start();
        reader.join();
        System.out.println("Read records from csv: " + recordList.size());
        
        // 2. Send post_uri to aws server
        System.out.println("Client starting ...... Time:" + getDate());
        System.out.println("All threads running..."); 
        List<Callable<Metrics>> postTasks = new ArrayList<>();
        String postUri = LOCAL_URI + "/load";
        int slidesCount = recordList.size() / threadNum;
        int start, end = 0;
        for (int i = 0; i < threadNum; i++) {
            start = i * slidesCount;
            end = i == threadNum - 1? recordList.size() : (i + 1) * slidesCount;
            postTasks.add(new PostTask(start, end, recordList, postUri));
        }
        
        List<Callable<Metrics>> getTasks = new ArrayList<>();
        //for (Record record : recordList) {
        //    getTasks.add(new GetTask(record, LOCAL_URI));
        //}
        long startTime = System.currentTimeMillis();
        runTasks(postTasks);
        //runTasks(getTasks);
        
        // 3. Metrics
        MetricUtils metricUtils = new MetricUtils();
        for (Callable<Metrics> task : getTasks) {
            metricUtils.add(((GetTask)task).getMetrics());
        }
        System.out.println("All threads complete... Time: " + getDate());
        //metricUtils.getMetrics();
        System.out.println("Total number of requests sent: " + 
                metricUtils.getSentRequestCount());
        System.out.println("Total number of successfully response: " + 
                metricUtils.getSuccessRequestCount());
        
        long runTime = System.currentTimeMillis() - startTime;
        System.out.println("Test Wall Time: " + runTime + " ms");
    }
    
    private static void runTasks(List<Callable<Metrics>> tasks) 
            throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        executorService.invokeAll(tasks);
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
    
    private static String getServerAddress(String ip, String port) {
        return new StringBuilder()
                .append("http://")
                .append(ip)
                .append(":")
                .append(port)
                .append("/RestfulWebServices/rest")
                .toString();
    }
    
    private static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void main(String[] args) throws Exception {
        // 10 35.167.118.155 8080
        if (args.length == 3) {
            threadNum = Integer.parseInt(args[0]);
            ip = args[1];
            port = args[2];
        }
        
        RestClient restClient = new RestClient();
        restClient.clientProcessing(threadNum, ip, port);
    }
}
