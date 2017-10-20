package com.entingwu.restfulwebservicesclient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Statistic {
    private int successRequestsNum = 0;
    private int sentRequestsNum = 0;
    private int totalRequest = 0;
    private long totalLatency = 0;
    private boolean isSorted = true;
    private List<Long> latencies;

    Statistic() {
        this.latencies = new ArrayList<>();
    }

    void add(Metrics metrics) {
        successRequestsNum += metrics.getSuccessCount();
        sentRequestsNum += metrics.getRequestCount();
        totalLatency += metrics.getTotalLatency();
        latencies.add(metrics.getTotalLatency());
        totalRequest++;
        isSorted = false;
    }

    long median(){
        return get(0.5);
    }

    long mean(){
        if(totalRequest ==0 ){
            return -1;
        }
        if(!isSorted){
            sort();
        }
        return totalLatency/totalRequest;
    }

    long get(double percentile){
        if(latencies.size() == 1){
            return latencies.get(0);
        }

        int index = Double.valueOf(percentile * latencies.size()).intValue() - 1;

        if(index >= latencies.size() || index <0 || latencies.isEmpty()){
            return -1;
        }

        return latencies.get(index);
    }

    int getSuccessRequestsNum() {
        return successRequestsNum;
    }

    int getSentRequestsNum() {
        return sentRequestsNum;
    }


    private void sort() {
        latencies.sort(Comparator.naturalOrder());
        isSorted = true;
    }
}
