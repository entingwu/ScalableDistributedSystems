package com.entingwu.logservice;

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
    
    public synchronized void putToDbQueryTimeList(int dbQueryTime) {
        dbQueryTimeList.add(Integer.toString(dbQueryTime));
    }
}
