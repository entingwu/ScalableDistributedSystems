package com.entingwu.restfulwebservicesclient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ClientThread implements Runnable {
    
    private ConcurrentLinkedQueue<Record> queue;
    private CyclicBarrier barrier;
    private AtomicBoolean isDone;
    private String uri;
    private long requestCount;
    private long successCount;
    private List<Long> latencies = new ArrayList<>();
    
    
    public ClientThread(ConcurrentLinkedQueue<Record> queue, String uri, 
            CyclicBarrier barrier, AtomicBoolean isDone) {
        this.queue = queue;
        this.uri = uri;
        this.barrier = barrier;
        this.isDone = isDone;
    }
    
    @Override
    public void run() {
        Client client = ClientBuilder.newClient();
        while(!isDone.get()) {
            Record record = null;
            if (!queue.isEmpty()) {
                record = queue.poll();
            } else {
                continue;
            }
            if (record == null) {
                isDone.set(true);
                break;
            }
            
            long start = System.currentTimeMillis();
            doPost(client, record, uri);
            long latency = System.currentTimeMillis() - start;
            latencies.add(latency);
            requestCount++;
            //start = System.currentTimeMillis();
            //doPost(webResource);
            //latency = System.currentTimeMillis() - start;
            //latencies.add(latency);
            //requestCount++;
        }

        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ex) {
           Logger.getLogger(ClientThread.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        RestClient.updateCount(requestCount, successCount);
        client.close();
    }
     
    private void doPost(Client client, Record record, String uri) {
        try {
            URL url = new URL("http", "localhost", 9090, "/RestfulWebServices/rest/load");
            System.out.println(url.toString());
            WebTarget target = client.target(url.toString());
            Response postResp = target.request()
                .post(Entity.json(record));
            if (postResp.getStatus() != 200) {
                throw new RuntimeException(
                        "Failed: HTTP post error: " + postResp.getStatus());
            }
            successCount++;
            postResp.close();
        } catch (RuntimeException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
    private void doGet(WebTarget target) throws ClientErrorException {
        try {
            Response getResp = target.request(MediaType.TEXT_PLAIN).get();
            if (getResp.getStatus() != 200) {
                throw new RuntimeException(
                        "Failed: HTTP get error: " + getResp.getStatus());
            }
            String str = getResp.readEntity(String.class);
            System.out.println("do get: " + str + ", id: " + Thread.currentThread().getId());
            successCount++;
            getResp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<Long> getLatencies() {
        return latencies;
    }
}
