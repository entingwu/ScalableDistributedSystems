package com.entingwu.logservice.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Singleton;

@Singleton
public class LogWriteCache {
    private static LogWriteCache instance;
    private List<String> postLogList;
    private List<String> postDbQueryTimeList; 
    private List<String> getLogList;
    
    public LogWriteCache() {
        postLogList = Collections.synchronizedList(new ArrayList<String>());
        postDbQueryTimeList = Collections.synchronizedList(new ArrayList<String>());
        getLogList = Collections.synchronizedList(new ArrayList<String>());
    }
    
    public static LogWriteCache getInstance() {
        if (instance == null) {
            instance = new LogWriteCache();
        }
        return instance;
    }
    
    public synchronized void putToPostLogCache(String log) {
        postLogList.add(log);
    }
    
    public synchronized void putToDbQueryTimeList(String dbQueryTime) {
        postDbQueryTimeList.add(dbQueryTime);
    }
    
    public synchronized void putToGetLogCache(String log) {
        getLogList.add(log);
    }
    
    public synchronized List<String> getPostLogCache() {
        List<String> data = postLogList;
        postLogList = Collections.synchronizedList(new ArrayList<String>());
        return data;
    }
    
    public synchronized List<String> getDbQueryTimeCache() {
        List<String> data = postDbQueryTimeList;
        postDbQueryTimeList = Collections.synchronizedList(new ArrayList<String>());
        return data;
    }
    
    public synchronized List<String> getGetLogCache() {
        List<String> data = getLogList;
        getLogList = Collections.synchronizedList(new ArrayList<String>());
        return data;
    }
    
    public synchronized int postSize() {
        return postLogList.size();
    }
    
    public synchronized int getSize() {
        return getLogList.size();
    }
}
