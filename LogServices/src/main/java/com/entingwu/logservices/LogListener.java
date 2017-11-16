package com.entingwu.logservices;

import com.amazonaws.services.sqs.model.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class LogListener implements ServletContextListener {

    private static final int SYNC_UP_SCHEDULE = 5 * 1000;
    private static long start = System.currentTimeMillis();
    private static String queueUrl;
    private static List<Message> receivedMsg;
    private static List<String> rawStrs;
    private static final ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(1);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("ServletContextListener started");
        queueUrl = SimpleQueueSubscriber.prepareSQS();
        receivedMsg = new ArrayList<>();
        List<Message> messages = SimpleQueueSubscriber.receiveSqsMessage();
        receivedMsg.addAll(messages);
        System.out.println("received: " + receivedMsg.size());
        int total = 0;
        for (int i = 0; i < receivedMsg.size(); i++) {
            String msg = receivedMsg.get(i).getBody();
            rawStrs.add(msg);
            total += msg.split(" ").length;
        }
        System.out.println("total:" + total);
        /*scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                getLogFromSQS();
            }}, 0, SYNC_UP_SCHEDULE, TimeUnit.MILLISECONDS);*/
    }

    private static void getLogFromSQS() {
        long curr = System.currentTimeMillis();
        if (curr - start > SYNC_UP_SCHEDULE) {
            List<Message> messages = SimpleQueueSubscriber.receiveSqsMessage();
            receivedMsg.addAll(messages);
            //SimpleQueueSubscriber.deleteSqsMessage(queueUrl, messages);
            System.out.println("here: " + receivedMsg.size());
            start = curr;
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        SimpleQueueSubscriber.displayMessage(receivedMsg);
    }
    
}
