package com.entingwu.jersey;

import com.entingwu.jersey.jdbc.RecordDAO;
import com.entingwu.jersey.model.Record;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RestServer {
    
    @GET
    @Path("/myvert/{skierID}&{dayNum}")
    @Produces(MediaType.APPLICATION_JSON)
    public Record getData(
            @PathParam("skierID") String skierID,
            @PathParam("dayNum") String dayNum) {
        RecordDAO dao = RecordDAO.getRecordDAO();
        Record record = dao.findRecordByFilter(skierID, dayNum);
        System.out.println("get: " + record.toString());
        return record;
    }
    
    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Record postData(Record record) {
        System.out.println("post: " + record.toString());
        RecordDAO dao = RecordDAO.getRecordDAO();
        dao.insert(record);
        return record;
    }
}
