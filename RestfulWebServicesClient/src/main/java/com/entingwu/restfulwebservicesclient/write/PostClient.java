package com.entingwu.restfulwebservicesclient.write;

import com.entingwu.restfulwebservicesclient.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class PostClient extends RestClient {
    
    private static final String POST_URI = REMOTE_URI + "/load";
    private static final String FILE_NAME = "post";
    private static int threadNum = 100;

    @Override
    public void clientProcessing(int threadNum, String ip, String port) 
            throws Exception {
        // 1. Read records from .csv
        super.clientProcessing(threadNum, ip, port);
        
        // 2. Send post_uri to aws server
        List<Callable<Metrics>> postTasks = new ArrayList<>();
        int dataSize = dataList.size();
        int slidesCount = dataSize / threadNum;
        if (slidesCount < 1) {
            postTasks.add(new PostTask(0, dataSize, dataList, POST_URI));
        } else {
            int start, end = 0;
            for (int i = 0; i < threadNum; i++) {
                start = i * slidesCount;
                end = i == threadNum - 1? dataSize : (i + 1) * slidesCount;
                postTasks.add(new PostTask(start, end, dataList, POST_URI));
            }
        }
        long startTime = System.currentTimeMillis();
        runTasks(postTasks, threadNum);
        
        // 3. Produce Metrics
        MetricUtils metricUtils = new MetricUtils();
        for (Callable<Metrics> task : postTasks) {
            metricUtils.add(((PostTask)task).getMetrics());
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
        
        PostClient postClient = new PostClient();
        postClient.clientProcessing(threadNum, ip, port);
    }
}
