package com.entingwu.jersey.cache;

import com.entingwu.jersey.model.RFIDLiftData;
import com.entingwu.jersey.model.SkiMetric;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

@Singleton
public class ReadCache {
    
    private static ReadCache instance;
    private Map<String, SkiMetric> tempCacheMap;
    private Map<String, SkiMetric> readCacheMap;
    
    public ReadCache() {
        tempCacheMap = new HashMap<>();
        readCacheMap = new HashMap<>();
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
    
    public synchronized Map<String, SkiMetric> getTempReadCache() {
        readCacheMap.putAll(tempCacheMap);
        System.out.println("readCacheMap: " + readCacheMap.size());
        Map<String, SkiMetric> data = tempCacheMap;
        tempCacheMap = new HashMap<>();
        return data;
    }
    
    public synchronized SkiMetric getSkiMetric(String key) {
        if (readCacheMap.containsKey(key)) {
            return readCacheMap.get(key);
        }
        return null;
    }
    
    public synchronized int size() {
        return tempCacheMap == null ? 0 : tempCacheMap.size();
    }
}
