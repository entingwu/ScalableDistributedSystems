package com.entingwu.restfulwebservicesclient;

import static java.net.HttpURLConnection.HTTP_OK;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class GetTask implements Callable<Metrics> {
        
    private final Record record;
    private final String uri;
    private Metrics metrics;
    private static AtomicInteger counter = new AtomicInteger();
    
    public GetTask(Record record, String uri) {
        this.record = record;
        this.uri = uri;
        this.metrics = new Metrics();
    }
    
    @Override
    public Metrics call() throws Exception {
        Client client = ClientBuilder.newClient();
        doGet(client);
        client.close();
        return metrics;
    }
    
    private void doGet(Client client) {
        long startTime = System.currentTimeMillis();
        String getUrl = uri + "/myvert/" + 
                record.getSkierID() + "&" + record.getDayNum();
        WebTarget target = client.target(getUrl);
        boolean isSent = false, isSuccess = false;
        try {
            Response response = target.request().get();
            if (response.getStatus() == HTTP_OK) {
                isSuccess = true;
                //System.out.println("do get: " + response.readEntity(String.class));
                counter.getAndIncrement();
                if (counter.get() % 100 == 0) {
                    System.out.println("Progress: " + counter.get());
                }
            }            
            isSent = true;
            response.close();
        } catch (ProcessingException e) {
            Logger.getLogger(GetTask.class.getName()).log(Level.SEVERE, null, e);
        }
        metrics.addRequest(
                System.currentTimeMillis() - startTime, isSent, isSuccess);
    }
    
    public Metrics getMetrics() {
        return metrics;
    }
}
