package com.entingwu.logservice;

import com.amazonaws.services.sqs.model.Message;
import com.entingwu.logservice.jdbc.DbQueryTimeDAO;
import com.entingwu.logservice.jdbc.LogDAO;
import com.entingwu.logservice.model.LogData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogService {
    
    private static final int SYNC_UP_SCHEDULE = 5 * 1000;
    private static final String PREFIX_DB = "D";
    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static long start = System.currentTimeMillis();
    private static Map<String, Message> receivedMap;
    private static List<Integer> responseTime;
    private static List<Integer> dbQueryTime;
    private static int errorCount = 0;
    private static int msgCount = 0;
    private static String queueUrl;
    private static final ScheduledExecutorService subscribeLogService =
            Executors.newScheduledThreadPool(1);
    
    public static void main(String[] args) {
        queueUrl = SimpleQueueSubscriber.prepareSQS();
        receivedMap = new HashMap<>();
        responseTime = new ArrayList<>();
        dbQueryTime = new ArrayList<>();
        subscribeLogService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                pullSqsMessage();
            }
        }, 0, SYNC_UP_SCHEDULE, TimeUnit.MILLISECONDS);
    }
    
    private static void pullSqsMessage() {
        long curr = System.currentTimeMillis();
        try {
            if (curr - start < SYNC_UP_SCHEDULE) {
                return;
            }
            List<Message> receiveMsgs = SimpleQueueSubscriber.receiveSqsMessage();
            List<Message> deleteMsgs = new ArrayList<>();
            if (receiveMsgs.isEmpty()) {
                return;
            }
            for (Message msg : receiveMsgs) {
                String messageId = msg.getMessageId();
                if (!receivedMap.keySet().contains(messageId)) {
                    receivedMap.put(messageId, msg);
                    deleteMsgs.add(msg);
                }
            }
            //SimpleQueueSubscriber.deleteSqsMessage(queueUrl, deleteMsgs);
            //convertMessageToLog();
            start = curr;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static void convertMessageToLog() {
        System.out.println("received: " + receivedMap.values().size());
        //DbQueryTimeDAO dbQueryDAO = DbQueryTimeDAO.getInstance();
        //LogDAO logDAO = LogDAO.getInstance();
        for (Message message : receivedMap.values()) {
            String msg = message.getBody();
            String[] rawData = msg.trim().split(SPACE);
            if (rawData[0].equals(PREFIX_DB)) {// db_time
                for (int i = 1; i < rawData.length; i++) {
                    /*try {
                        dbQueryDAO.insertDbQueryData(Integer.parseInt(rawData[i]));
                        //dbQueryTime.add(Integer.parseInt(rawData[i]));
                    } catch (SQLException ex) {
                        Logger.getLogger(LogService.class.getName()).log(Level.SEVERE, null, ex);
                    }*/
                }
            } else { // response_time, error_num
                for (int i = 0; i < rawData.length; i++) {
                    String[] pair = rawData[i].trim().split(COMMA);
                    LogData log = new LogData(
                            Integer.parseInt(pair[0]), 
                            Integer.parseInt(pair[1]));
                    /*try {
                        logDAO.insertLogData(log);
                        //responseTime.add(Integer.parseInt(pair[0]));
                        //errorCount += Integer.parseInt(pair[1]); 
                    } catch (SQLException ex) {
                        Logger.getLogger(LogService.class.getName()).log(Level.SEVERE, null, ex);
                    }*/
                }
            }
            msgCount += rawData.length;
        }
        System.out.println("total:" + msgCount);
        System.out.println("errorCount:" + errorCount);
        System.out.println("dbqueryTime:" + dbQueryTime.toString());
        System.out.println("responseTime:" + responseTime.toString());
    }
}
