package com.entingwu.restfulwebservicesclient;

import static java.net.HttpURLConnection.HTTP_OK;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class PostTask implements Callable<Metrics> {
    
    private static final String PROTOCOL = "http";
    private static final String FILE = "/RestfulWebServices1/rest/load";
    
    private Metrics metrics;
    private int start;
    private int end;
    private List<Record> records;
    private final String host;
    private int port;
    
    public PostTask(int start, int end, List<Record> records, String ip, int port) {
        this.start = start;
        this.end = end;
        this.records = records;
        this.host = ip;
        this.port = port;
        metrics = new Metrics();
    }
    
    @Override
    public Metrics call() throws Exception {
        Client client = ClientBuilder.newClient();
        for (int i = start; i < end; i++) {
            doPost(client, records.get(i));
        }
        client.close();
        return metrics;
    }
    
    private void doPost(Client client, Record record) throws MalformedURLException {
        long startTime = System.currentTimeMillis();
        URL url = new URL(PROTOCOL, host, port, FILE);
        //WebTarget webTarget = client.target(url.toString());
        //System.out.println("url: " + url.toString());
        WebTarget webTarget = client.target(url.toString());
        Response response = null;
        boolean isSent = false;
        try {
            response = webTarget.request().post(Entity.json(record));
            //System.out.println("do post: " + response.readEntity(String.class));
            response.close();
            isSent = true;
        } catch (ProcessingException e) {
            e.printStackTrace();
        }
        metrics.addRequest(System.currentTimeMillis() - startTime, isSent, 
                isSent && response.getStatus() == HTTP_OK);
    }
    
    public Metrics getMetrics() {
        return metrics;
    }
}
