package com.entingwu.restfulwebservicesclient;

import java.util.ArrayList;
import java.util.List;

public class Metrics {
    private int successCount = 0;
    private int requestCount = 0;
    private List<Long> latencies = new ArrayList<>();
    
    public void addRequest(long latency, boolean isSent, boolean isSuccess) {
        latencies.add(latency);
        requestCount += isSent? 1 : 0;
        successCount += isSuccess? 1 : 0;
    }
    
    public int getSuccessCount() {
        return successCount;
    }
    
    public int getRequestCount() {
        return requestCount;
    }
    
    public List<Long> getLatencies() {
        return latencies;
    }
}
