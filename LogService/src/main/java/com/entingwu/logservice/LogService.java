package com.entingwu.logservice;

import com.entingwu.logservice.cache.LogWriteCache;
import com.amazonaws.services.sqs.model.Message;
import com.entingwu.logservice.cache.LogData;
import com.entingwu.logservice.jdbc.DbQueryTimeDAO;
import com.entingwu.logservice.jdbc.LogDAO;
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
    
    private static final String LOG_SERVICE = LogService.class.getSimpleName();
    private static final int SYNC_UP_SCHEDULE = 5 * 1000;
    private static final int SYNC_UP_DB_SCHEDULE = 5 * 1000;
    private static final int BATCH_COUNT = 100;
    private static final String PREFIX_DB = "D";
    private static final String PREFIX_GET = "G";
    public static final String SPACE = " ";
    public static final String COMMA = ",";
    private static Map<String, Message> receivedMap;
    private static String queueUrl;
    private static long start = System.currentTimeMillis();
    private static int msgCount = 0;
    private static LogWriteCache cache;
    private static LogDAO logDAO;
    private static DbQueryTimeDAO dbQueryDAO;
    private static final ScheduledExecutorService logSubscriberService =
            Executors.newScheduledThreadPool(1);
    private static final ScheduledExecutorService logPublisherService = 
            Executors.newScheduledThreadPool(1);
    
    public static void main(String[] args) {
        logDAO = LogDAO.getInstance();
        dbQueryDAO = DbQueryTimeDAO.getInstance();
        cache = LogWriteCache.getInstance();
        queueUrl = SimpleQueueSubscriber.prepareSQS();
        receivedMap = new HashMap<>();
        logSubscriberService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                pullSqsMessage();
            }
        }, 0, SYNC_UP_SCHEDULE, TimeUnit.MILLISECONDS);
        
        logPublisherService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                syncLogCacheToDB();
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
            String messageId;
            if (receiveMsgs.isEmpty()) {
                return;
            }
            for (Message message : receiveMsgs) {
                messageId = message.getMessageId();
                // Get new message
                if (!receivedMap.keySet().contains(messageId)) {
                    receivedMap.put(messageId, message);
                    deleteMsgs.add(message);
                    putMessageInCache(message.getBody());
                    //System.out.println("received: " + receivedMap.values().size());
                }
            }
            SimpleQueueSubscriber.deleteSqsMessage(queueUrl, deleteMsgs);
            start = curr;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static void putMessageInCache(String msg) {
        String[] rawData = msg.trim().split(SPACE);
        if (rawData[0].equals(PREFIX_DB)) { // db_query_time
            String postdbTime = msg.substring(2);
            System.out.println("postdbtime: " + postdbTime);
            if (postdbTime.length() != 0) {
                cache.putToDbQueryTimeList(postdbTime);
            }
        } else if (rawData[0].equals(PREFIX_GET)) {
            System.out.println("getlog: " + msg);
            cache.putToGetLogCache(msg.substring(2));
        } else { // response_time, error_num
            System.out.println("postlog: " + msg);
            cache.putToPostLogCache(msg);
        }
    }
    
    private static List<LogData> convertToLogData(List<String> logCache, 
            boolean isPost) {
        List<LogData> logCacheList = new ArrayList<>();
        for (String message : logCache) {
            String[] rawDataList = message.trim().split(SPACE);
            for (String rawData : rawDataList) {
                String[] pair = rawData.trim().split(COMMA);
                LogData log = isPost 
                        ? new LogData(Integer.parseInt(pair[0]), 0, 
                                      Integer.parseInt(pair[1]), 1) 
                        : new LogData(Integer.parseInt(pair[0]), 
                                      Integer.parseInt(pair[0]), 
                                      Integer.parseInt(pair[1]), 0);
                logCacheList.add(log);
            }
            msgCount += rawDataList.length;
        }
        //System.out.println("total:" + msgCount);
        return logCacheList;
    }
    
    private static List<Integer> convertToDbQuerytTime() {
        List<Integer> dbQueryTimeList = new ArrayList<>();
        for (String message : cache.getDbQueryTimeCache()) {
            String[] rawDataList = message.trim().split(SPACE);
            for (String rawData : rawDataList) {
                dbQueryTimeList.add(Integer.parseInt(rawData));
            }
        }
        return dbQueryTimeList;
    }
    
    private static void syncLogCacheToDB() {
        long curr = System.currentTimeMillis();
        if (cache.postSize() > BATCH_COUNT || 
                (curr - start > SYNC_UP_DB_SCHEDULE) && 
                (cache.postSize() > 0 || cache.getSize() > 0) ) {
            List<LogData> postlogCacheList = 
                    convertToLogData(cache.getPostLogCache(), true);
            List<LogData> getlogCacheList = 
                    convertToLogData(cache.getGetLogCache(), false);
            List<Integer> dbQueryTimeList = convertToDbQuerytTime(); // post
            try {
                logDAO.batchInsertLogDAO(postlogCacheList);
                logDAO.batchInsertLogDAO(getlogCacheList);
                dbQueryDAO.batchInsertPostLogDAO(dbQueryTimeList);
            } catch (SQLException ex) {
                Logger.getLogger(LOG_SERVICE).log(Level.SEVERE, null, ex);
            }
        }
    }
}
