package com.entingwu.jersey.cache;

import com.entingwu.jersey.model.RFIDLiftData;
import com.entingwu.jersey.model.SkiMetric;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Singleton;

@Singleton
public class ReadCache {
    
    private static ReadCache instance;
    private ConcurrentHashMap<String, SkiMetric> tempCacheMap;
    private ConcurrentHashMap<String, SkiMetric> readCacheMap;
    
    public ReadCache() {
        tempCacheMap = new ConcurrentHashMap<>();
        readCacheMap = new ConcurrentHashMap<>();
    }
    
    public static ReadCache getInstance() {
        if (instance == null) {
            instance = new ReadCache();
        }
        return instance;
    }
    
    public synchronized void putToReadCache(RFIDLiftData record) {
        String key = record.getID();
        if (!tempCacheMap.containsKey(key)) {
            SkiMetric value = new SkiMetric(key, record.getSkierID(), 
                    record.getDayNum(), record.getVertical(), 1);
            tempCacheMap.put(key, value);
        } else {
            SkiMetric value = tempCacheMap.get(key);
            value.update(record);
        }
    }
    
    public synchronized void putToReadCacheFromDB(SkiMetric skiMetric) {
        readCacheMap.putIfAbsent(skiMetric.getID(), skiMetric);
    }
    
    public synchronized ConcurrentHashMap<String, SkiMetric> getTempReadCache() {
        readCacheMap.putAll(tempCacheMap);
        ConcurrentHashMap<String, SkiMetric> data = tempCacheMap;
        tempCacheMap = new ConcurrentHashMap<>();
        return data;
    }
    
    public synchronized SkiMetric getSkiMetric(String key) {
        if (readCacheMap.containsKey(key)) {
            return readCacheMap.get(key);
        }
        return null;
    }
    
    public synchronized int size() {
        return tempCacheMap.size();
    }
}
