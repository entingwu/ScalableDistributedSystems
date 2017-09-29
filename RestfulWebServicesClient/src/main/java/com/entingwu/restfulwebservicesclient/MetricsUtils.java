package com.entingwu.restfulwebservicesclient;

import java.util.Arrays;
import java.util.List;

public class MetricsUtils {
    
    public void getMetrics(List<ClientThread> threads, long runTime, 
            long requestCnt, long successCnt) {
        int size = (int)requestCnt;
        long[] latencyArray = new long[size];
        long totalLatencies = 0;
        int i = 0;
        for (ClientThread thread : threads) {
            List<Long> latencies = thread.getLatencies();
            for (long latency : latencies) {
                totalLatencies += latency;
                latencyArray[i++] = latency;
            }
        }
        Arrays.sort(latencyArray);
        
        double meanLatency = getMeanLatency(totalLatencies, successCnt);
        double medianLatency = getMedianLatency(latencyArray, size);
        long p99Latency = latencyArray[(int)(size * 0.99 - 1)];
        long p95Latency = latencyArray[(int)(size * 0.95 - 1)];
        print(meanLatency, medianLatency, p99Latency, p95Latency);
    }
    
    private double getMeanLatency(long totalLatencies, long successCount) {
        return totalLatencies / successCount * 1.0;
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

    private static void print(double mean, double median, long p99Latency, 
            long p95Latency) {
        System.out.println("Mean latency for all requests: " + mean + " ms");
        System.out.println("Median latency for all requests: " + median + " ms");
        System.out.println("99th percentile latency: " + p99Latency + " ms");
        System.out.println("95th percentile latency: " + p95Latency + " ms");
    }
}
