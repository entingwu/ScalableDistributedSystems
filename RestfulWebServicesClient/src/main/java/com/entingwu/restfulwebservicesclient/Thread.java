package com.entingwu.restfulwebservicesclient;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;

public class Thread implements Runnable {
    
    private final String uri;
    private final int iterationNum;
    private long requestCount;
    private long successCount;
    private List<Long> latencies = new ArrayList<>();
    
    public Thread(String uri, int iterationNum) {
        this.uri = uri;
        this.iterationNum = iterationNum;
    }
    
    @Override
    public void run() {
        Client client = ClientBuilder.newClient();
        WebTarget webResource = client.target(uri);
        for (int i = 0; i < iterationNum; i++) {
            long start = System.currentTimeMillis();
            doGet(webResource);
            long latency = System.currentTimeMillis() - start;
            latencies.add(latency);
            requestCount++;

            start = System.currentTimeMillis();
            doPost(webResource);
            latency = System.currentTimeMillis() - start;
            latencies.add(latency);
            requestCount++;
        }
        client.close();
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
    
    public long getRequestCount() {
        return requestCount;
    }
    
    public List<Long> getLatencies() {
        return latencies;
    }
    
    public long getSuccessCount() {
        return successCount;
    }
}
