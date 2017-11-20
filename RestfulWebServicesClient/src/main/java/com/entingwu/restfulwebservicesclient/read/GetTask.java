package com.entingwu.restfulwebservicesclient.read;

import com.entingwu.restfulwebservicesclient.Metrics;
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
    
    private static final String DAY_NUM = "999";
    private final int start;
    private final int end;
    private final String uri;
    private Metrics metrics;
    private static AtomicInteger counter = new AtomicInteger();
    
    public GetTask(int start, int end, String uri) {
        this.start = start;
        this.end = end;
        this.uri = uri;
        this.metrics = new Metrics();
    }
    
    @Override
    public Metrics call() throws Exception {
        Client client = ClientBuilder.newClient();
        for (int i = start; i <= end; i++) {
            doGet(client, String.valueOf(i));
        }

        client.close();
        return metrics;
    }
    
    private void doGet(Client client, String skierId) {
        long startTime = System.currentTimeMillis();
        String getUrl = uri + "/myvert/" + skierId + "&" + DAY_NUM;
        WebTarget target = client.target(getUrl);
        boolean isSent = false, isSuccess = false;
        try {
            Response response = target.request().get();
            if (response.getStatus() == HTTP_OK) {
                isSuccess = true;
                //System.out.println("do get: " + response.readEntity(SkiMetric.class));
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
