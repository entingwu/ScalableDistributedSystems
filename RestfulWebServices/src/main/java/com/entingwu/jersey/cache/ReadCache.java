package com.entingwu.jersey.cache;

import com.entingwu.jersey.model.RFIDLiftData;
import com.entingwu.jersey.model.SkiMetric;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

@Singleton
public class ReadCache {
    
    private static ReadCache instance;
    private Map<String, SkiMetric> readCacheMap;
    
    public ReadCache() {
        readCacheMap = new HashMap<>();
    }
    
    public static ReadCache getInstance() {
        if (instance == null) {
            instance = new ReadCache();
        }
        return instance;
    }
    
    public synchronized void putToReadCache(RFIDLiftData record) {
        String key = record.getSkierID() + "&" + record.getDayNum();
        if (!readCacheMap.containsKey(key)) {
            SkiMetric value = new SkiMetric(key, record.getSkierID(), 
                    record.getDayNum(), record.getVertical(), 1);
            readCacheMap.put(key, value);
        } else {
            SkiMetric value = readCacheMap.get(key);
            value.update(record);
        }
    }
    
    public synchronized Map<String, SkiMetric> getReadCache() {
        Map<String, SkiMetric> data = readCacheMap;
        return data;
    }
    
    public synchronized int size() {
        return readCacheMap == null ? 0 : readCacheMap.size();
    }
}
