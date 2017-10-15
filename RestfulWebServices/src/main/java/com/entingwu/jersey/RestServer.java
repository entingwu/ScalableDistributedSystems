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
import javax.ws.rs.core.Response;

@Path("/")
//@Produces(MediaType.APPLICATION_JSON)
public class RestServer {
    
    private static Map<String, Record> map = new HashMap<>();
    
    @GET
    @Path("/{myvert}/{skierID}&{dayNum}")
    @Consumes(value = "text/plain")
    public Response getData(
            @PathParam("myvert") String myvert,
            @PathParam("skierID") String skierID,
            @PathParam("dayNum") String dayNum) {
        System.out.println("map size" + map.size());
        String vertical = myvert;
        String liftNum = "0";
        if (map.containsKey(skierID)) {
            Record data = map.get(skierID);
            liftNum = String.valueOf(data.getLiftID());
        }
        String str = new StringBuilder()
                .append(vertical)
                .append(",")
                .append(liftNum)
                .toString();
        return Response.status(200).entity(str).build();
    }
    
    @POST
    @Path("/load")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Record postData(Record record) {
        System.out.println(record.toString());
        return record;
    }
}
