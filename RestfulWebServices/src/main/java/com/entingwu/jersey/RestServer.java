package com.entingwu.jersey;

import com.entingwu.jersey.cache.CacheSyncWorker;
import com.entingwu.jersey.cache.ReadCache;
import com.entingwu.jersey.cache.WriteCache;
import com.entingwu.jersey.jdbc.RFIDLiftDAO;
import com.entingwu.jersey.jdbc.SkiMetricDAO;
import com.entingwu.jersey.model.RFIDLiftData;
import com.entingwu.jersey.model.SkiMetric;
import java.sql.SQLException;
import javax.annotation.PostConstruct;
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
        System.out.println("Load rest server");
    }
    
    @GET
    @Path("/myvert/{skierID}&{dayNum}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getData(
            @PathParam("skierID") String skierID,
            @PathParam("dayNum") String dayNum) throws SQLException {
        SkiMetricDAO skiMetricDAO = SkiMetricDAO.getSkiMetricDAO();
        SkiMetric skiMetric = skiMetricDAO.findSkiMetricByFilter(skierID, dayNum); 
        System.out.println("get: " + skiMetric.toString());
        return skiMetric.toString();
    }
    
    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postData(RFIDLiftData record) throws SQLException {
        System.out.println("post: " + record.getSkierID());
        // cache
        WriteCache writeCache = WriteCache.getInstance();
        writeCache.putToWriteCache(record);
        ReadCache readCache = ReadCache.getInstance();
        readCache.putToReadCache(record);
        
        // no cache
        /*RFIDLiftDAO dao = RFIDLiftDAO.getRFIDLiftDAO();
        dao.insertRFIDLift(record);
        SkiMetricDAO skiMetricDAO = SkiMetricDAO.getSkiMetricDAO();
        skiMetricDAO.upsertSkiMetric(record);*/
        return record.toString();
    }
}
