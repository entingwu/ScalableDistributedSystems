package com.entingwu.restfulwebservicesclient.read;

import com.entingwu.restfulwebservicesclient.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GetClient extends RestClient {
    
    private static final String FILE_NAME = "get";
    private static final int SKIER_NUM = 40000;
    private static int threadNum = 100;

    @Override
    public void clientProcessing(int threadNum, String ip, String port) 
            throws Exception {
        // 1. Read records from .csv
        super.clientProcessing(threadNum, ip, port);
        
        // 2. Send get_uri to aws server
        List<Callable<Metrics>> getTasks = new ArrayList<>();
        int skiersPerThread = SKIER_NUM / threadNum;
        if (skiersPerThread < 1) {
            getTasks.add(new GetTask(0, SKIER_NUM, REMOTE_URI));
        } else {
            int start, end = 0;
            for (int i = 0; i < threadNum; i++) {
                start = i * skiersPerThread + 1;
                end = (i + 1) * skiersPerThread;
                getTasks.add(new GetTask(start, end, REMOTE_URI));
            }
        }
        long startTime = System.currentTimeMillis();
        runTasks(getTasks, threadNum);
        
        // 3. Produce Metrics
        MetricUtils metricUtils = new MetricUtils();
        for (Callable<Metrics> task : getTasks) {
            metricUtils.add(((GetTask)task).getMetrics());
        }
        System.out.println("All threads complete... Time: " + getDate());
        metricUtils.getMetrics();        
        long runTime = System.currentTimeMillis() - startTime;
        System.out.println("Test Wall Time: " + runTime + " ms");
        DataOutput dataOutput = new DataOutput();
        dataOutput.generateChart(metricUtils.getLatencies(), FILE_NAME);
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 3) {
            threadNum = Integer.parseInt(args[0]);
            ip = args[1];
            port = args[2];
        }
        
        GetClient getClient = new GetClient();
        getClient.clientProcessing(threadNum, ip, port);
    }
}
