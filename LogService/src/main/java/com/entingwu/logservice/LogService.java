package com.entingwu.logservice;

import com.amazonaws.services.sqs.model.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LogService {
    
    private static final int SYNC_UP_SCHEDULE = 5 * 1000;
    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static long start = System.currentTimeMillis();
    private static List<Message> receivedMsg;
    private static List<Integer> responseTime;
    private static int errorCount = 0;
    private static int msgCount = 0;
    private static String queueUrl;
    private static final ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService convertExecutorService =
            Executors.newScheduledThreadPool(1);
    
    public static void main(String[] args) {
        queueUrl = SimpleQueueSubscriber.prepareSQS();
        receivedMsg = new ArrayList<>();
        responseTime = new ArrayList<>();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getMsgFromSQS();
            }
        }, 0, SYNC_UP_SCHEDULE, TimeUnit.MILLISECONDS);
    }
    
    private static void getMsgFromSQS() {
        long curr = System.currentTimeMillis();
        if (curr - start > SYNC_UP_SCHEDULE) {
            List<Message> messages = SimpleQueueSubscriber.receiveSqsMessage();
            if (messages.size() > 0) {
                Map<String, Message> msgMap = new HashMap<>();
                for (Message msg : messages) {
                    if (!msgMap.keySet().contains(msg.getBody())) {
                        msgMap.put(msg.getBody(), msg);
                    }
                }
                receivedMsg.addAll(msgMap.values());
                try {
                    SimpleQueueSubscriber.deleteSqsMessage(queueUrl, new ArrayList<>(msgMap.values()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                convertMsgToLog();
            }
            start = curr;
        }
    }
    
    private static void convertMsgToLog() {
        System.out.println("received: " + receivedMsg.size());
        for (int i = 0; i < receivedMsg.size(); i++) {
            String msg = receivedMsg.get(i).getBody();
            String[] rawData = msg.split(SPACE);
            msgCount += rawData.length;
            for (int j = 0; j < rawData.length; j++) {
                String[] pair = rawData[j].split(COMMA);
                responseTime.add(Integer.parseInt(pair[0]));
                errorCount += Integer.parseInt(pair[1]); 
            }
        }
        System.out.println("total:" + msgCount);
        System.out.println("errorCount:" + errorCount);
        System.out.println("responseTime:" + responseTime.toString());
    }
}
