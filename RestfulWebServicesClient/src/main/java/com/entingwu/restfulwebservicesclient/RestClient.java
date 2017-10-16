package com.entingwu.restfulwebservicesclient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class RestClient {
    
    private static final String REST_CLIENT = RestClient.class.getName();
//    private static final String LOCAL_URI = 
//            "http://localhost:9090/RestfulWebServices/rest";
    private static String ip = "35.167.118.155";
    private static String port = "8080";
    private static String remoteUri = getServerAddress(ip, port);
    private static int threadNum = 200;
    
    protected ConcurrentLinkedQueue<Record> queue = null;
    private AtomicBoolean isDone = new AtomicBoolean(false);
    private ExecutorService executor;
    private List<ClientThread> threads = new ArrayList<>();
    static CyclicBarrier barrier;
    private static long requestSum;
    private static long successSum;

    public long clientProcessing(int threadNum, String ip, String port) {
        queue = new ConcurrentLinkedQueue<>();
        executor = getExecutor(threadNum);
        barrier = new CyclicBarrier(threadNum);

        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                 DataReader.readFile(queue);
            }
        });
        reader.start();

        long start = System.currentTimeMillis();
        System.out.println("All threads running...");
        
        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            ClientThread thread = new ClientThread(queue, remoteUri, barrier, isDone);
            threads.add(thread);
            Future future = executor.submit(thread);
            futures.add(future);
        }
         
        for (Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(REST_CLIENT).log(Level.SEVERE, null, ex);
            }
        }
        
        executor.shutdown();
        System.out.println("All threads complete... Time: " + getDate());
        return System.currentTimeMillis() - start;
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

    private ExecutorService getExecutor(int threads) {
        if (executor == null) {
            executor = Executors.newFixedThreadPool(threads);
        }
        return executor;
    }
    
    public static synchronized void updateCount(
            long requestDelta, long successDelta) {
        requestSum += requestDelta;
        successSum += successDelta;
    }
    
    private static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    public List<ClientThread> getThreads() {
        return threads;
    }

    public static void main(String[] args) {
        // 10 35.167.118.155 8080
        System.out.println("thread_num server_ip server_port");
        if (args.length == 3) {
            threadNum = Integer.parseInt(args[0]);
            ip = args[1];
            port = args[2];
        }
        
        RestClient restClient = new RestClient();
  
        System.out.println("Client starting ...... Time:" + getDate());
        long runTime = restClient.clientProcessing(
                threadNum, ip, String.valueOf(port));
        System.out.println("Total Number of requests sent: " + requestSum);
        System.out.println("Total Number of Successful responses: " + 
                successSum);
        
        //MetricUtils metrics = new MetricUtils();
        //metrics.getMetrics(restClient.getThreads(), runTime, 
        //        requestSum, successSum);
        System.out.println("Test Wall Time: " + runTime + " ms");
    }
}
