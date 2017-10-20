package com.entingwu.restfulwebservicesclient;

public class Metrics {
    private int successCount = 0;
    private int requestCount = 0;
    private long totalLatency = 0;
    
    public void addRequest(long latency, boolean isSent, boolean isSuccess) {
        totalLatency += latency;
        requestCount += isSent? 1 : 0;
        successCount += isSuccess? 1 : 0;
    }
    
    public int getSuccessCount() {
        return successCount;
    }
    
    public int getRequestCount() {
        return requestCount;
    }
    
    public long getTotalLatency() {
        return totalLatency;
    }
}
