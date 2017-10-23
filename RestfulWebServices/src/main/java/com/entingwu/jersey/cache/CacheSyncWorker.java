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
    private static final int SYNC_UP_SCHEDULE = 10 * 1000;
    private static final int BATCH_COUNT = 50;
    private static RFIDLiftDAO rfidLiftDAO;
    private static SkiMetricDAO skiMetricDAO;
    private static long start = System.currentTimeMillis();
    private static final ScheduledExecutorService scheduledExecutorService =
    Executors.newScheduledThreadPool(1);
    
    public static void init() {
          System.out.println("begin here");
        rfidLiftDAO = RFIDLiftDAO.getRFIDLiftDAO();
        skiMetricDAO = SkiMetricDAO.getSkiMetricDAO();
        ScheduledFuture scheduledFuture =
            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("CacheSyncWorker here");
                WriteCache writeCache = WriteCache.getInstance();
                long curr = System.currentTimeMillis();
                if (writeCache.size() > BATCH_COUNT || 
                    ((curr - start > SYNC_UP_SCHEDULE) && writeCache.size() != 0)) {
                    List<RFIDLiftData> batchData = writeCache.getWriteCache();
                    try {
                        rfidLiftDAO.batchInsertRFIDLift(batchData);
                        batchData.clear();
                        start = curr;
                    } catch (SQLException ex) {
                        Logger.getLogger(CACHE_SYNC_WORKER).log(Level.SEVERE, null, ex);
                    }
                }
                
                ReadCache readCache = ReadCache.getInstance(); 
                if (readCache.size() > BATCH_COUNT || 
                    (curr - start > SYNC_UP_SCHEDULE) && readCache.size() != 0) {
                    Map<String, SkiMetric> dataMap = readCache.getReadCache();
                    try {
                        if (!dataMap.isEmpty()) {
                            List<SkiMetric> batchSkiMetric = 
                                    new ArrayList<>(dataMap.values());
                            skiMetricDAO.batchUpsertSkiMetric(batchSkiMetric);
                            dataMap.clear();
                        }
                        start = curr;
                    } catch (SQLException ex) {
                        Logger.getLogger(CACHE_SYNC_WORKER).log(Level.SEVERE, null, ex);
                    }
                }
            }}, 0 , SYNC_UP_SCHEDULE , TimeUnit.MILLISECONDS);
    }
}
