package com.entingwu.restfulwebservicesclient;

import static java.net.HttpURLConnection.HTTP_OK;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class ClientThread implements Runnable {
    
    private static final String CLIENT_THREAD = ClientThread.class.getName();
    private static AtomicInteger kk = new AtomicInteger(0);
    private List<Record> recordList;
    private AtomicBoolean isDone;
    private String uri;
    private long requestCount;
    private long successCount;
    private List<Long> latencies = new ArrayList<>();
    public int start = 0;
    public int end;
      
    public ClientThread(List<Record> recordList, String uri, AtomicBoolean isDone) {
        this.recordList = recordList;
        this.uri = uri;
        this.isDone = isDone;
        this.end = recordList.size();
    }
    
    @Override
    public void run() {
        Client client = ClientBuilder.newClient();
        int iter = 0;
        for (int i = start ; i <end ; ++ i ) {
            Record record = recordList.get(i);
            long start = System.currentTimeMillis();
            doPost(client, record, uri);
            long latency = System.currentTimeMillis() - start;
            latencies.add(latency);
            requestCount++;
            
        }
        
        System.out.println("Done: Thread id: " + Thread.currentThread().getId() + " is done");
        //RestClient.updateCount(requestCount, successCount);
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
                kk.getAndIncrement();
                if (kk.get() % 100 ==0) {
                    System.out.println("Progress: is : " + kk.get());
                }
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