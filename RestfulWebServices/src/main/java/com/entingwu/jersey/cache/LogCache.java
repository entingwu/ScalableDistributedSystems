package com.entingwu.jersey.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Singleton;

@Singleton
public class LogCache {
    private static LogCache instance;
    private List<String> logCacheList;
    
    public LogCache() {
        logCacheList = Collections.synchronizedList(new ArrayList<String>());
    }
   
    public static LogCache getInstance() {
        if (instance == null) {
            instance = new LogCache();
        }
        return instance;
    }
    
    public synchronized void putToLogCache(String log) {
        logCacheList.add(log);
    }
    
    public synchronized List<String> getLogCache() {
        List<String> data = logCacheList;
        logCacheList = Collections.synchronizedList(new ArrayList<String> ());
        return data;
    }
   
    public synchronized int size() {
        return logCacheList.size();
    }
}
