package com.entingwu.restfulwebservicesclient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

public class RestClient {
    
    private static int threadNum = 10;
    private static int iterationNum = 100;
    private static String ip = "35.167.118.155";
    private static int port = 8080;
    private ExecutorService executor;
    private List<Thread> threads = new ArrayList<>();

    public long clientProcessing(int threadNum, final int iterationNum,
            String ip, String port) {
        executor = getExecutor(threadNum);
        final String uri = getServerAddress(ip, port);
        //final String uri = "http://localhost:9090/RestfulWebServices/rest/server";
        System.out.println("URI: " + uri);
        long start = System.currentTimeMillis();
        System.out.println("All threads running...");
        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            Thread thread = new Thread(uri, iterationNum);
            threads.add(thread);
            Future future = executor.submit(thread);
            futures.add(future);
        }
        
        for (Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(
                        RestClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        executor.shutdown();
        System.out.println("All threads complete... Time: " + getDate());
        return System.currentTimeMillis() - start;
    }

    private ExecutorService getExecutor(int threads) {
        if (executor == null) {
            executor = Executors.newFixedThreadPool(threads);
        }
        return executor;
    }

    private String getServerAddress(String ip, String port) {
        return new StringBuilder()
                .append("http://")
                .append(ip)
                .append(":")
                .append(port)
                .append("/RestfulWebServices/rest/server")
                .toString();
    }
    
    private static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    public List<Thread> getThreads() {
        return threads;
    }

    public static void main(String[] args) {
        // Command: client 10 100 35.167.118.155 8080
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please input "
                    + "'client thread_num iteration_num server_ip server_port',"
                    + "split by space");
        while (scanner.hasNext()) {
            String[] strs = scanner.nextLine().split("\\s");
            if (strs != null && strs.length == 5) {
                threadNum = Integer.parseInt(strs[1]);
                iterationNum = Integer.parseInt(strs[2]);
                ip = strs[3];
                port = Integer.parseInt(strs[4]);
                break;
            }
        }
        
        RestClient restClient = new RestClient();
        System.out.println("Client starting ...... Time:" + getDate());
        long runTime = restClient.clientProcessing(
                threadNum, iterationNum, ip, String.valueOf(port));
        MetricsUtils metrics = new MetricsUtils();
        metrics.getMetrics(restClient.getThreads(), runTime);
        System.out.println("Test Wall Time: " + runTime);
    }
}
