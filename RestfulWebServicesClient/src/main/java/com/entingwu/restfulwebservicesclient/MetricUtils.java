package com.entingwu.restfulwebservicesclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MetricUtils {
    private int successCount = 0;
    private int requestCount = 0;
    private long totalLatency = 0;
    private List<Long> latencies;

    public MetricUtils() {
        this.latencies = new ArrayList<>();
    }

    public void add(Metrics metrics) {
        successCount += metrics.getSuccessCount();
        requestCount += metrics.getRequestCount();
        latencies.addAll(metrics.getLatencies());       
    }

    public void getMetrics() {
        int size = latencies.size();
        Object[] latencyArrayObj = latencies.toArray();
        long[] latencyArray = new long[size];
        for (int i = 0; i < size; ++i) {
            latencyArray[i] = (long)latencyArrayObj[i];
            totalLatency += latencyArray[i];        
        }

        Arrays.sort(latencyArray);
        double meanLatency = getMeanLatency();
        double medianLatency = getMedianLatency(latencyArray, size);
        long p99Latency = latencyArray[(int)(size * 0.99) - 1];
        long p95Latency = latencyArray[(int)(size * 0.95) - 1];
        print(meanLatency, medianLatency, p99Latency, p95Latency);
    }
    
    private double getMeanLatency() {
        return totalLatency / requestCount * 1.0;
    }
    
    private double getMedianLatency(long[] latencyArray, int size) {
        double median = 0.0;
        if (size % 2 == 1) {
            median = latencyArray[size / 2];
        } else {
            median = (latencyArray[size / 2] 
                    + latencyArray[size / 2 - 1]) / 2 * 1.0;
        }
        return median;
    }

    public int getSuccessRequestCount() {
        return successCount;
    }

    public int getSentRequestCount() {
        return requestCount;
    }
    
    private void print(double mean, double median, long p99Latency, 
            long p95Latency) {
        System.out.println("Total number of requests sent: " + requestCount);
        System.out.println("Total number of successfully response: " + successCount);
        System.out.println("Mean latency for all requests: " + mean + " ms");
        System.out.println("Median latency for all requests: " + median + " ms");
        System.out.println("99th percentile latency: " + p99Latency + " ms");
        System.out.println("95th percentile latency: " + p95Latency + " ms");
    }
}
