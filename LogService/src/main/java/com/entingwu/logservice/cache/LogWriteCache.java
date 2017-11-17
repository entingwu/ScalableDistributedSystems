package com.entingwu.logservice.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Singleton;

@Singleton
public class LogWriteCache {
    private static LogWriteCache instance;
    private List<String> logCacheList;
    private List<String> dbQueryTimeList; 
    
    public LogWriteCache() {
        logCacheList = Collections.synchronizedList(new ArrayList<String>());
        dbQueryTimeList = Collections.synchronizedList(new ArrayList<String>());
    }
    
    public static LogWriteCache getInstance() {
        if (instance == null) {
            instance = new LogWriteCache();
        }
        return instance;
    }
    
    public synchronized void putToLogCache(String log) {
        logCacheList.add(log);
    }
    
    public synchronized void putToDbQueryTimeList(String dbQueryTime) {
        dbQueryTimeList.add(dbQueryTime);
    }
    
    public synchronized List<String> getLogCache() {
        List<String> data = logCacheList;
        logCacheList = Collections.synchronizedList(new ArrayList<String>());
        return data;
    }
    
    public synchronized List<String> getDbQueryTimeList() {
        List<String> data = dbQueryTimeList;
        dbQueryTimeList = Collections.synchronizedList(new ArrayList<String>());
        return data;
    }
    
    public synchronized int size() {
        return logCacheList.size();
    }
}
