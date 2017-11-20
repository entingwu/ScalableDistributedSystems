package com.entingwu.jersey;

import com.entingwu.jersey.cache.CacheSyncWorker;
import com.entingwu.jersey.cache.LogCache;
import com.entingwu.jersey.cache.ReadCache;
import com.entingwu.jersey.cache.WriteCache;
import com.entingwu.jersey.jdbc.RFIDLiftDAO;
import com.entingwu.jersey.jdbc.SkiMetricDAO;
import com.entingwu.jersey.model.RFIDLiftData;
import com.entingwu.jersey.model.SkiMetric;
import java.sql.SQLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RestServer { 
    private static final String COMMA = ",";
    static {
        CacheSyncWorker.init();
    }
    
    @GET
    @Path("/myvert/{skierID}&{dayNum}")
    @Produces(MediaType.APPLICATION_JSON)
    public SkiMetric getData(
            @PathParam("skierID") String skierID,
            @PathParam("dayNum") String dayNum) throws SQLException {
        ReadCache readCache = ReadCache.getInstance();
        SkiMetric skiMetric = getDataWithNoCache(skierID, dayNum, readCache);
        return skiMetric;
    }
    
    private SkiMetric getDataWithNoCache(String skierID, String dayNum, 
            ReadCache readCache) throws SQLException {
        SkiMetricDAO skiMetricDAO = SkiMetricDAO.getInstance();
        SkiMetric skiMetric = new SkiMetric();
        long startTime = System.currentTimeMillis();
        int errorNum = 0;
        try {
            skiMetric = skiMetricDAO.findSkiMetricByFilter(skierID, dayNum);  
        } catch(SQLException ex) {
            errorNum = 1;
        }
        long responseTime = System.currentTimeMillis() - startTime;
        String log = responseTime + COMMA + errorNum;
        LogCache logCache = LogCache.getInstance();
        logCache.putToGetCache(log);
        /*if (skiMetric != null) {
            readCache.putToReadCacheFromDB(skiMetric);
        }*/
        return skiMetric;
    }
    
    private SkiMetric getDataWithCache(String skierID, String dayNum, 
            ReadCache readCache) throws SQLException {
        String key = RFIDLiftData.getID(skierID, dayNum);
        SkiMetric skiMetric = readCache.getSkiMetric(key);
        if (skiMetric != null) {
            return skiMetric;
        }
        return getDataWithNoCache(skierID, dayNum, readCache);
    }
    
    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postData(RFIDLiftData record) throws SQLException {
        long startTime = System.currentTimeMillis();
        int errorNum = 0;
        try {
            postDataWithCache(record);
        } catch(SQLException ex) {
            errorNum = 1;
        }
        long responseTime = System.currentTimeMillis() - startTime;
        String log = responseTime + COMMA + errorNum;
        LogCache logCache = LogCache.getInstance();
        logCache.putToPostCache(log);
        return record.toString();
    }
    
    private void postDataWithCache(RFIDLiftData record) throws SQLException {
        WriteCache writeCache = WriteCache.getInstance();
        writeCache.putToWriteCache(record);
        ReadCache readCache = ReadCache.getInstance();
        readCache.putToReadCache(record);
    }
    
    private void postDataWithNoCache(RFIDLiftData record) throws SQLException {
        RFIDLiftDAO dao = RFIDLiftDAO.getInstance();
        dao.insertRFIDLift(record);
        SkiMetricDAO skiMetricDAO = SkiMetricDAO.getInstance();
        skiMetricDAO.upsertSkiMetric(record);
    }
}
