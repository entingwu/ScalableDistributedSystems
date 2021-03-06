package com.entingwu.jersey;

import com.entingwu.jersey.cache.CacheSyncWorker;
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
        SkiMetric skiMetric = getDataWithCache(skierID, dayNum, readCache);
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
    
    private SkiMetric getDataWithNoCache(String skierID, String dayNum, 
            ReadCache readCache) throws SQLException {
        SkiMetricDAO skiMetricDAO = SkiMetricDAO.getInstance();
        SkiMetric skiMetric = skiMetricDAO.findSkiMetricByFilter(skierID, dayNum);
        if (skiMetric != null) {
            readCache.putToReadCacheFromDB(skiMetric);
        }
        return skiMetric;
    }
    
    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postData(RFIDLiftData record) throws SQLException {
        postDataWithCache(record);
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
