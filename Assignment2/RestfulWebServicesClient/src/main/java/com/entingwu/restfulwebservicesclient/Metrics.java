package com.entingwu.restfulwebservicesclient;

import java.util.ArrayList;
import java.util.List;

public class Metrics {
    private int successCount = 0;
    private int requestCount = 0;
    private List<Long> latencies = new ArrayList<>();
    
    public void addRequest(long latency, boolean isSent, boolean isSuccess) {
        requestCount += isSent? 1 : 0;
        successCount += isSuccess? 1 : 0;
        latencies.add(latency);
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
