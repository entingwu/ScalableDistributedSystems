package com.entingwu.restfulwebservicesclient.read;

import com.entingwu.restfulwebservicesclient.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetClient extends RestClient {
    
    private static final String GET_CLIENT = GetClient.class.getName();
    private static final String FILE_NAME = "get";
    private static final int SKIER_NUM = 10000; //3000;//40000;
    private static int threadNum = 100;

    @Override
    public void clientProcessing(int threadNum, String ip, String port) {
        // 1. Read records from .csv
        super.clientProcessing(threadNum, ip, port);
        
        // 2. Send get_uri to server
        List<Callable<Metrics>> getTasks = new ArrayList<>();
        int skiersPerThread = SKIER_NUM / threadNum;
        if (skiersPerThread < 1) {
            getTasks.add(new GetTask(0, SKIER_NUM, LOCAL_URI));
        } else {
            for (int i = 0; i < threadNum; i++) {
                getTasks.add(new GetTask(
                        i * skiersPerThread + 1,   // start
                        (i + 1) * skiersPerThread, // end 
                        REMOTE_URI));
            }
        }
        long startTime = System.currentTimeMillis();
        try {
            runTasks(getTasks, threadNum);
        } catch (InterruptedException ex) {
            Logger.getLogger(GET_CLIENT).log(Level.SEVERE, null, ex);
        }
        
        // 3. Emit Metrics
        MetricUtils metricUtils = new MetricUtils();
        for (Callable<Metrics> task : getTasks) {
            metricUtils.add(((GetTask)task).getMetrics());
        }
        System.out.println("All threads complete... Time: " + getDate());
        metricUtils.getMetrics();        
        long runTime = System.currentTimeMillis() - startTime;
        System.out.println("Test Wall Time: " + runTime + " ms");
        
        // 4. Draw chart
        DataOutput dataOutput = new DataOutput();
        try {
            dataOutput.generateChart(metricUtils.getLatencies(), FILE_NAME);
        } catch (IOException ex) {
            Logger.getLogger(GET_CLIENT).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        if (args.length == 3) {
            threadNum = Integer.parseInt(args[0]);
            ip = args[1];
            port = args[2];
        }
        GetClient getClient = new GetClient();
        getClient.clientProcessing(threadNum, ip, port);
    }
}
