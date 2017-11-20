package com.entingwu.jersey.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Singleton;

@Singleton
public class LogCache {
    private static LogCache instance;
    private List<String> postCacheList;
    private List<String> postDbTimeList;
    private List<String> getCacheList;
    
    public LogCache() {
        postCacheList = Collections.synchronizedList(new ArrayList<String>());
        postDbTimeList = Collections.synchronizedList(new ArrayList<String>());
        getCacheList = Collections.synchronizedList(new ArrayList<String>());
    }
   
    public static LogCache getInstance() {
        if (instance == null) {
            instance = new LogCache();
        }
        return instance;
    }
    
    public synchronized void putToPostCache(String log) {
        postCacheList.add(log);
    }
    
    public synchronized void putToPostDbTimeList(long dbQueryTime) {
        postDbTimeList.add(Long.toString(dbQueryTime));
    }
    
    public synchronized void putToGetCache(String log) {
        getCacheList.add(log);
    }
    
    public synchronized List<String> getPostLogCache() {
        List<String> data = postCacheList;
        postCacheList = Collections.synchronizedList(new ArrayList<String>());
        return data;
    }
    
    public synchronized List<String> getDbQueryTimeCache() {
        List<String> data = postDbTimeList;
        postDbTimeList = Collections.synchronizedList(new ArrayList<String>());
        return data;
    }
    
    public synchronized List<String> getGetLogCache() {
        List<String> data = getCacheList;
        getCacheList = Collections.synchronizedList(new ArrayList<String>());
        return data;
    }
   
    public synchronized int postSize() {
        return postCacheList.size();
    }
    
    public synchronized int getSize() {
        return getCacheList.size();
    }
}
