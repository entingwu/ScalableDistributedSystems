package com.entingwu.jersey.cache;

import com.entingwu.jersey.jdbc.RFIDLiftDAO;
import com.entingwu.jersey.jdbc.SkiMetricDAO;
import com.entingwu.jersey.model.RFIDLiftData;
import com.entingwu.jersey.model.SkiMetric;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CacheSyncWorker {
    
    private static final String CACHE_SYNC_WORKER = CacheSyncWorker.class.getName();
    private static final int SYNC_UP_SCHEDULE = 5 * 1000;
    private static final int BATCH_COUNT = 100;
    private static RFIDLiftDAO rfidLiftDAO;
    private static SkiMetricDAO skiMetricDAO;
    private static long start = System.currentTimeMillis();
    private static final ScheduledExecutorService scheduledExecutorService =
    Executors.newScheduledThreadPool(1);
    
    public static void init() {
        System.out.println("begin here");
        rfidLiftDAO = RFIDLiftDAO.getInstance();
        skiMetricDAO = SkiMetricDAO.getInstance();
        ScheduledFuture scheduledFuture =
            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("CacheSyncWorker here");
                syncWriteCacheToDB();
                syncReadCacheToDB();
            }}, 0 , SYNC_UP_SCHEDULE , TimeUnit.MILLISECONDS);
    }
    
    private static void syncWriteCacheToDB() {
        long curr = System.currentTimeMillis();
        WriteCache writeCache = WriteCache.getInstance();
        if (writeCache.size() > BATCH_COUNT || 
            ((curr - start > SYNC_UP_SCHEDULE) && writeCache.size() != 0)) {
            List<RFIDLiftData> batchData = writeCache.getWriteCache();
            try {
                rfidLiftDAO.batchInsertRFIDLift(batchData);
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
            Map<String, SkiMetric> dataMap = readCache.getTempReadCache();
            try {
                if (!dataMap.isEmpty()) {
                    List<SkiMetric> batchSkiMetric = 
                            new ArrayList<>(dataMap.values());
                    skiMetricDAO.batchUpsertSkiMetric(batchSkiMetric);
                }
                start = curr;
            } catch (SQLException ex) {
                Logger.getLogger(CACHE_SYNC_WORKER).log(Level.SEVERE, null, ex);
            }
        }
    }
}
