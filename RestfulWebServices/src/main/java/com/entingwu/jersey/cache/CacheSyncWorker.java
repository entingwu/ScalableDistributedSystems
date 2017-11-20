package com.entingwu.jersey.cache;

import com.entingwu.jersey.SimpleQueuePublisher;
import com.entingwu.jersey.jdbc.RFIDLiftDAO;
import com.entingwu.jersey.jdbc.SkiMetricDAO;
import com.entingwu.jersey.model.RFIDLiftData;
import com.entingwu.jersey.model.SkiMetric;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CacheSyncWorker {
    
    private static final String CACHE_SYNC_WORKER = CacheSyncWorker.class.getName();
    private static final String SPACE = " ";
    private static final int SYNC_UP_SCHEDULE = 5 * 1000;
    private static final int BATCH_COUNT = 100;
    private static RFIDLiftDAO rfidLiftDAO;
    private static SkiMetricDAO skiMetricDAO;
    private static long start = System.currentTimeMillis();
    private static final ScheduledExecutorService scheduledExecutorService =
        Executors.newScheduledThreadPool(1);
    // Log
    private static final int SYNC_UP_LOG_SCHEDULE = 10 * 1000;
    private static final int MESSAGE_NUM = 10 * 1000;
    private static final String PREFIX_DB = "D ";
    private static final String PREFIX_GET = "G ";
    private static List<String> postLogMsgs;
    private static List<String> getLogMsgs;
    private static long startPost = System.currentTimeMillis();
    private static long startGet = System.currentTimeMillis();
    private static final ScheduledExecutorService logPublisherService =
        Executors.newScheduledThreadPool(1);
    
    public static void init() {
        SimpleQueuePublisher.prepareSQS();
        postLogMsgs = new ArrayList<>();
        getLogMsgs = new ArrayList<>();
        rfidLiftDAO = RFIDLiftDAO.getInstance();
        skiMetricDAO = SkiMetricDAO.getInstance();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                syncWriteCacheToDB();
                syncReadCacheToDB();
            }}, 0 , SYNC_UP_SCHEDULE , TimeUnit.MILLISECONDS);
        
        logPublisherService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                sendPostLogCacheToSQS();
                sendGetLogCacheToSQS();
            }}, 0 , SYNC_UP_LOG_SCHEDULE , TimeUnit.MILLISECONDS);
    }
    
    private static void sendPostLogCacheToSQS() {
        long curr = System.currentTimeMillis();
        LogCache logCache = LogCache.getInstance();
        if (logCache.postSize() > BATCH_COUNT || 
            (curr - startPost > SYNC_UP_LOG_SCHEDULE) && logCache.postSize() > 0) {
            StringBuilder sb = new StringBuilder();
            createMessage(postLogMsgs, logCache.getPostLogCache(), sb);
            sb = new StringBuilder().append(PREFIX_DB);
            createMessage(postLogMsgs, logCache.getDbQueryTimeCache(), sb);
            startPost = curr;
        }
        syncLogsToSQS(postLogMsgs);
    }
    
    private static void sendGetLogCacheToSQS() {
        long curr = System.currentTimeMillis();
        LogCache logCache = LogCache.getInstance();
        if (logCache.getSize() > BATCH_COUNT || 
            (curr - startGet > SYNC_UP_LOG_SCHEDULE) && logCache.getSize() > 0) {
            StringBuilder sb = new StringBuilder().append(PREFIX_GET);
            createMessage(getLogMsgs, logCache.getGetLogCache(), sb);
            startGet = curr;
        }
        syncLogsToSQS(getLogMsgs);
    }
    
    private static void createMessage(List<String> messages, List<String> cache, StringBuilder sb) {
        int i = 0;
        while (i < cache.size()) {
            sb.append(cache.get(i)).append(SPACE);
            if (i != 0 && i % MESSAGE_NUM == 0) {
                messages.add(sb.toString());
                sb = new StringBuilder();
            }
            i++;
        }
        messages.add(sb.toString());
    }
    
    private static void syncLogsToSQS(List<String> messages) {
        if (messages.size() > 0) {
            SimpleQueuePublisher.sendBatchMsgToSQS(messages);
            messages.clear();
        }
    }
    
    private static void syncWriteCacheToDB() {
        WriteCache writeCache = WriteCache.getInstance();
        LogCache logCache = LogCache.getInstance();
        long curr = System.currentTimeMillis();
        if (writeCache.size() > BATCH_COUNT || 
            ((curr - start > SYNC_UP_SCHEDULE) && writeCache.size() != 0)) {
            List<RFIDLiftData> batchData = writeCache.getWriteCache();
            try {
                rfidLiftDAO.batchInsertRFIDLift(batchData);
                logCache.putToPostDbTimeList(System.currentTimeMillis() - curr);
                start = curr;
            } catch (SQLException ex) {
                Logger.getLogger(CACHE_SYNC_WORKER).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void syncReadCacheToDB() {
        ReadCache readCache = ReadCache.getInstance(); 
        LogCache logCache = LogCache.getInstance();
        long curr = System.currentTimeMillis();
        if (readCache.size() > BATCH_COUNT || 
            (curr - start > SYNC_UP_SCHEDULE) && readCache.size() != 0) {
            ConcurrentHashMap<String, SkiMetric> dataMap = readCache.getTempReadCache();
            try {
                if (!dataMap.isEmpty()) {
                    List<SkiMetric> batchSkiMetric = 
                            Collections.synchronizedList(
                                    new ArrayList<>(dataMap.values()));
                    skiMetricDAO.batchUpsertSkiMetric(batchSkiMetric);
                    logCache.putToPostDbTimeList(System.currentTimeMillis() - curr);
                }
                start = curr;
            } catch (SQLException ex) {
                Logger.getLogger(CACHE_SYNC_WORKER).log(Level.SEVERE, null, ex);
            }
        }
    }
}
