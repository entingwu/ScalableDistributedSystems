package com.entingwu.jersey;

import com.entingwu.jersey.model.Record;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class RestServer {
    
    private static Map<String, Record> map = new HashMap<>();
    
    @GET
    @Path("/myvert/{skierID}&{dayNum}")
    @Produces(MediaType.APPLICATION_JSON)
    public Record getData(
            @PathParam("skierID") String skierID,
            @PathParam("dayNum") String dayNum) {
        Record record = new Record();
        if (map.containsKey(skierID)) {
            record = map.get(skierID);
            System.out.println("get: " + record.toString());
        }
        return record;
    }
    
    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Record postData(Record record) {
        System.out.println("post: " + record.toString());
        if (!map.containsKey(record.getSkierID())) {
            map.put(record.getSkierID(), record);
        }
        return record;
    }
}
