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
import java.util.concurrent.CyclicBarrier;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestClient {
    
    private static int threadNum = 100;
    private static int iterationNum = 100;
    private static String ip = "35.167.118.155";
    private static int port = 8080;
    
    private ExecutorService executor;
    private List<ClientThread> threads = new ArrayList<>();
    static CyclicBarrier barrier;
    private static long requestSum;
    private static long successSum;

    public long clientProcessing(int threadNum, final int iterationNum,
            String ip, String port) {
        executor = getExecutor(threadNum);
        barrier = new CyclicBarrier(threadNum);
        final String uri = getServerAddress(ip, port);
        //final String uri = "http://localhost:9090/RestfulWebServices/rest/server";
        System.out.println("URI: " + uri);
        long start = System.currentTimeMillis();
        System.out.println("All threads running...");
        
        List<Future> futures = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            ClientThread thread = new ClientThread(uri, iterationNum, barrier);
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
        // 10 100 35.167.118.155 8080
        System.out.println("thread_num iteration_num server_ip server_port");
        if (args.length == 4) {
            threadNum = Integer.parseInt(args[0]);
            iterationNum = Integer.parseInt(args[1]);
            ip = args[2];
            port = Integer.parseInt(args[3]);
        }
        
        RestClient restClient = new RestClient();
        restClient.test();
        /*System.out.println("Client starting ...... Time:" + getDate());
        long runTime = restClient.clientProcessing(
                threadNum, iterationNum, ip, String.valueOf(port));
        System.out.println("Total Number of requests sent: " + requestSum);
        System.out.println("Total Number of Successful responses: " + 
                successSum);
        
        MetricUtils metrics = new MetricUtils();
        metrics.getMetrics(restClient.getThreads(), runTime, 
                requestSum, successSum);
        System.out.println("Test Wall Time: " + runTime + " ms");*/
    }
    
    public void test() {
        String myvert = "enting";
        String skierID = "01";
        int dayNum = 1;
        String getUri = "http://localhost:9090/RestfulWebServices/rest/" 
                + myvert + "/" + skierID + "&" + dayNum;
        System.out.println("URI: " + getUri);
        
        String resortID = "resortID";
        String timestamp = getDate();
        String liftID = "3";
        String postUri = "http://localhost:9090/RestfulWebServices/rest/" 
                + "load/" + resortID + "&" + dayNum + "&" + timestamp 
                + "&" + skierID + "&" + liftID;
        //System.out.println("URI: " + getUri);
        
        Client client = ClientBuilder.newClient();
        testGet(client.target(getUri));
        //testPost(client.target(postUri));
    }
    
    private static void testGet(WebTarget target) {
        Response getResp = target.request(MediaType.TEXT_PLAIN).get();
        String str = getResp.readEntity(String.class);
        System.out.println("get result: " + str);
        getResp.close();
    }
    
    private static void testPost(WebTarget target) {
        //Response postResp = target.request().post(Entity.);

    }
}
