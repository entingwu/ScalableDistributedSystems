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
    private static final int MESSAGE_NUM = 10000;
    private static List<String> messages;
    private static long startCache = System.currentTimeMillis();
    private static final ScheduledExecutorService logExecutorService =
        Executors.newScheduledThreadPool(1);
    
    public static void init() {
        messages = new ArrayList<>();
        SimpleQueuePublisher.prepareSQS();
        rfidLiftDAO = RFIDLiftDAO.getInstance();
        skiMetricDAO = SkiMetricDAO.getInstance();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                syncWriteCacheToDB();
                syncReadCacheToDB();
            }}, 0 , SYNC_UP_SCHEDULE , TimeUnit.MILLISECONDS);
        
        logExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                putLogCacheToList();
                syncLogsToSQS();
            }}, 0 , SYNC_UP_LOG_SCHEDULE , TimeUnit.MILLISECONDS);
    }
    
    private static void putLogCacheToList() {
        long curr = System.currentTimeMillis();
        LogCache logCache = LogCache.getInstance();
        if (logCache.size() > BATCH_COUNT || 
            (curr - startCache > SYNC_UP_LOG_SCHEDULE) && logCache.size() > 0) {
            List<String> logData = logCache.getLogCache();
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < logData.size()) {
                sb.append(logData.get(i)).append(SPACE);
                if (i != 0 && i % MESSAGE_NUM == 0) {
                    messages.add(sb.toString());
                    sb = new StringBuilder();
                }
                i++;
            }
            messages.add(sb.toString());
            startCache = curr;
        }
    }
    
    private static void syncLogsToSQS() {
        if (messages.size() > 0) {
            SimpleQueuePublisher.sendBatchMsgToSQS(messages);
            messages = new ArrayList<>();
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
                long databaseQueryTime = System.currentTimeMillis() - curr;
                logCache.putToDbQueryTimeList(databaseQueryTime);
                // put to log cache
                start = curr;
            } catch (SQLException ex) {
                Logger.getLogger(CACHE_SYNC_WORKER).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void syncReadCacheToDB() {
        long curr = System.currentTimeMillis();
        ReadCache readCache = ReadCache.getInstance(); 
        if (readCache.size() > BATCH_COUNT || 
            (curr - start > SYNC_UP_SCHEDULE) && readCache.size() != 0) {
            ConcurrentHashMap<String, SkiMetric> dataMap = readCache.getTempReadCache();
            try {
                if (!dataMap.isEmpty()) {
                    List<SkiMetric> batchSkiMetric = 
                            Collections.synchronizedList(
                                    new ArrayList<>(dataMap.values()));
                    skiMetricDAO.batchUpsertSkiMetric(batchSkiMetric);
                }
                start = curr;
            } catch (SQLException ex) {
                Logger.getLogger(CACHE_SYNC_WORKER).log(Level.SEVERE, null, ex);
            }
        }
    }
}
