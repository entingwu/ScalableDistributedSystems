package com.entingwu.restfulwebservicesclient;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class MetricsUtils {
    private class LatenciesComparator implements Comparator<Long> {
        @Override
        public int compare(Long o1, Long o2) {
            return(int)(o1 - o2);
        } 
    }
    
    public void getMetrics(List<Thread> threads, long runTime) {
        PriorityQueue<Long> priorityQueue = 
                new PriorityQueue<>(1000, new LatenciesComparator());
        long totalLatencies = 0;
        long requestCnt = 0;
        long successCnt = 0;
        for (Thread thread : threads) {
            List<Long> latencies = thread.getLatencies();
            for (long latency : latencies) {
                totalLatencies += latency;
                priorityQueue.offer(latency);
            }
            requestCnt += thread.getRequestCount();
            successCnt += thread.getSuccessCount();
        }
        
        int size = priorityQueue.size();
        long[] queueArray = new long[size];
        int i = 0;
        while (!priorityQueue.isEmpty()) {
            queueArray[i++] = priorityQueue.poll();
        }
        
        double meanLatency = getMeanLatency(totalLatencies, successCnt);
        double medianLatency = getMedianLatency(queueArray, size);
        long p99Latency = getTopKlatency(queueArray, (int)(size * 0.99 - 1));
        long p95Latency = getTopKlatency(queueArray, (int)(size * 0.95 - 1));
        print(runTime, requestCnt, successCnt, meanLatency, medianLatency, 
                p99Latency, p95Latency);
    }
    
    private double getMeanLatency(long totalLatencies, long successCount) {
        return totalLatencies / successCount * 1.0;
    }
    
    private double getMedianLatency(long[] queueArray, int size) {
        double median = 0.0;
        if (size % 2 == 1) {
            median = queueArray[size / 2];
        } else {
            median = (queueArray[size / 2] + queueArray[size / 2 - 1]) / 2 *1.0;
        }
        return median;
    }
    
    private long getTopKlatency(long[] queueArray, int index) {
        return queueArray[index];
    }
    
    private static void print(long runTime, long requestCnt, long successCnt, 
            double mean, double median, long p99Latency, long p95Latency) {
        System.out.println("Total Number of requests sent: " + requestCnt);
        System.out.println("Total Number of Successful responses: " + successCnt);
        System.out.println("Mean latency for all requests: " + mean);
        System.out.println("Median latency for all requests: " + median);
        System.out.println("99th percentile latency: " + p99Latency);
        System.out.println("95th percentile latency: " + p95Latency);
    }
}
