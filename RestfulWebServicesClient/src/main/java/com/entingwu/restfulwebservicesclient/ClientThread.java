package com.entingwu.restfulwebservicesclient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;

public class ClientThread implements Runnable {
    
    private final BlockingQueue<String> queue;
    private final int iterationNum;
    private long requestCount;
    private long successCount;
    private List<Long> latencies = new ArrayList<>();
    private CyclicBarrier barrier;
    
    public ClientThread(BlockingQueue<String> queue, 
            int iterationNum, CyclicBarrier barrier) {
        this.queue = queue;
        this.iterationNum = iterationNum;
        this.barrier = barrier;
    }
    
    @Override
    public void run() {
        Client client = ClientBuilder.newClient();
        while(true) {
            String uri = queue.poll();
            WebTarget webResource = client.target(uri);
            //for (int i = 0; i < iterationNum; i++) {
                long start = System.currentTimeMillis();
                //doGet(webResource);
                long latency = System.currentTimeMillis() - start;
                //latencies.add(latency);
                //requestCount++;

                //start = System.currentTimeMillis();
                doPost(webResource);
                latency = System.currentTimeMillis() - start;
                latencies.add(latency);
                requestCount++;
            //}

            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                Logger.getLogger(ClientThread.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
            RestClient.updateCount(requestCount, successCount);
        }
        //client.close();
    }
    
    private void doGet(WebTarget target) throws ClientErrorException {
        try {
            Response getResp = target.request(MediaType.TEXT_PLAIN).get();
            if (getResp.getStatus() != 200) {
                throw new RuntimeException(
                        "Failed: HTTP get error: " + getResp.getStatus());
            }
            successCount++;
            getResp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void doPost(WebTarget target) {
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap<>();
        map.add("content", "entingwu");
        try {
            Response postResp = target.request().post(Entity.form(map));
            if (postResp.getStatus() != 200) {
                throw new RuntimeException(
                        "Failed: HTTP post error: " + postResp.getStatus());
            }
            successCount++;
            postResp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    
    public List<Long> getLatencies() {
        return latencies;
    }
}
