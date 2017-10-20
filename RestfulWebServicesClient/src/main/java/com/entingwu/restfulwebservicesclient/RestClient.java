package com.entingwu.restfulwebservicesclient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class RestClient {
    
    private static final String REST_CLIENT = RestClient.class.getName();
    private static final String LOCAL_URI = 
            "http://localhost:9090/RestfulWebServices/rest";
    private static String ip = "35.167.118.155";
    private static String port = "8080";
    private static String remoteUri = getServerAddress(ip, port);
    private static int threadNum = 32;
    
    protected ArrayList<Record> queue = null;
    private AtomicBoolean isDone = new AtomicBoolean(false);
    private ExecutorService executor;
    private List<ClientThread> threads = new ArrayList<>();
    static CyclicBarrier barrier;
    private static long requestSum;
    private static long successSum;

    public long clientProcessing(int threadNum, String ip, String port) throws Exception {
        queue = new ArrayList<>();
        executor = getExecutor(threadNum);
        barrier = new CyclicBarrier(threadNum);

        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                 DataReader.readFile(queue);
            }
        });
        reader.start();
        reader.join();
        System.out.println("queue size is" + queue.size());
        
        int slidesCount = queue.size()/threadNum;

        long start = System.currentTimeMillis();
        System.out.println("All threads running...");
        
        /*DataReader dataReader = new DataReader();
        List<Record> records = dataReader.get();
        System.out.println("finished reading from file: " + records.size());
//        
        List<Callable<Metrics>> postTasks = new ArrayList<>();
        int size = records.size();
        int len = size;
        for (int i = 0; i < size; i += len) {
            int end = i + len < size ? i + len : size;
            postTasks.add(new PostTask(i, end, records, ip, Integer.parseInt(port)));
        }
        long start = System.currentTimeMillis();
        runTasks(postTasks);
        
        Statistic statics = new Statistic();
        for (Callable<Metrics> task : postTasks) {
            if (task instanceof PostTask) {
                statics.add(((PostTask)task).getMetrics());
            }
        }
        System.out.println("Total number of requests sent: " + statics.getSentRequestsNum());
        System.out.println("Total number of successfully response: " + statics.getSuccessRequestsNum());*/
        
        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            ClientThread thread = new ClientThread(queue, remoteUri, barrier, isDone);
            thread.start = i * slidesCount;
            thread.end = i == threadNum - 1? queue.size() : (i + 1) * slidesCount;
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
                .append("/RestfulWebServices2/rest")
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

    public static void main(String[] args) throws Exception {
        // 10 35.167.118.155 8080
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
    
    private static void runTasks(List<Callable<Metrics>> tasks) 
            throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.invokeAll(tasks);
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
}
