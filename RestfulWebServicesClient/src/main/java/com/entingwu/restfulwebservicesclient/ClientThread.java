package com.entingwu.restfulwebservicesclient;

import static java.net.HttpURLConnection.HTTP_OK;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

public class ClientThread implements Runnable {
    
    private static final String CLIENT_THREAD = ClientThread.class.getName();
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
        //ClientConfig configuration = new ClientConfig();
        //configuration.property(ClientProperties.CONNECT_TIMEOUT, 10000);
        //configuration.property(ClientProperties.READ_TIMEOUT, 10000);
        //Client client = ClientBuilder.newClient(configuration);
        Client client = ClientBuilder.newClient();
        while(!isDone.get()) {
            Record record = null;
            if (!queue.isEmpty()) {
                record = queue.poll();
            } else {
                continue;
            }
            if (record.flag) {
                System.out.println("Distributed file system done");
                isDone.set(true);
                break;
            }
            
            long start = System.currentTimeMillis();
            doPost(client, record, uri);
            long latency = System.currentTimeMillis() - start;
            latencies.add(latency);
            requestCount++;
            
            //start = System.currentTimeMillis();
            //doGet(client, record, uri);
            //latency = System.currentTimeMillis() - start;
            //latencies.add(latency);
            //requestCount++;
        }

        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ex) {
           Logger.getLogger(CLIENT_THREAD).log(Level.SEVERE, null, ex);
        }
        RestClient.updateCount(requestCount, successCount);
        client.close();
    }
     
    private void doPost(Client client, Record record, String uri) {
        String postUrl = uri + "/load";
        //System.out.println(postUrl);
        try {
            WebTarget target = client.target(postUrl);
            Response response = target.request()
                .post(Entity.json(record));
            if (response.getStatus() == HTTP_OK) {
                successCount++;
                System.out.println(response.getEntity().toString());
            }
            response.close();
        } catch (ProcessingException e) {
            Logger.getLogger(CLIENT_THREAD).log(Level.SEVERE, null, e);
        }
    }
    
    private void doGet(Client client, Record record, String uri) {
        String getUrl = uri + "/myvert/" + 
                record.getSkierID() + "&" + record.getDayNum();
        //System.out.println(getUrl);
        try {
            WebTarget target = client.target(getUrl);
            Response response = target.request().get();
            if (response.getStatus() == HTTP_OK) {
                successCount++;
            }            
            response.close();
        } catch (ProcessingException e) {
            Logger.getLogger(CLIENT_THREAD).log(Level.SEVERE, null, e);
        }
    }
    
    public List<Long> getLatencies() {
        return latencies;
    }
}