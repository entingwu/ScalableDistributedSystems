package com.entingwu.jersey.cache;

import com.entingwu.jersey.model.RFIDLiftData;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;

@Singleton
public class WriteCache {
    
    private static WriteCache instance;
    private List<RFIDLiftData> writeCacheList;
    
    public WriteCache() {
        writeCacheList = new ArrayList<>();
    }
    
    public static WriteCache getInstance() {
        if (instance == null) {
            instance = new WriteCache();
        }
        return instance;
    }
    
    public synchronized void putToWriteCache(RFIDLiftData record) {
        writeCacheList.add(record);
    }
    
    public synchronized List<RFIDLiftData> getWriteCache() {
        List<RFIDLiftData> data = writeCacheList;
        return data;
    }
    
    public int size() {
        return writeCacheList == null ? 0 : writeCacheList.size();
    }
}
