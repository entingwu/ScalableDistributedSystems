package com.entingwu.jersey;

import com.entingwu.jersey.model.RFIDLiftData;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
public class RestServer {
    
    private static Map<String, RFIDLiftData> map = new HashMap<>();
    
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
            RFIDLiftData data = map.get(skierID);
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
    @Path("/load/{resortID}&{dayNum}&{timestamp}&{skierID}&{liftID}")
    @Produces(value = "text/plain")
    public Response postData(
            @PathParam("resortID") String resortID,
            @PathParam("dayNum") String dayNum,
            @PathParam("timestamp") String timestamp,
            @PathParam("skierID") String skierID,
            @PathParam("liftID") String liftID) {
        RFIDLiftData data = new RFIDLiftData(
                Integer.parseInt(resortID), 
                Integer.parseInt(dayNum), 
                Integer.parseInt(skierID), 
                Integer.parseInt(liftID), 
                Integer.parseInt(timestamp));
        if (!map.containsKey(skierID)) {
            map.put(skierID, data);
        }
        String str = new StringBuilder()
                .append(resortID)
                .append(dayNum)
                .append(timestamp)
                .append(skierID)
                .append(liftID)
                .toString();
        return Response.status(200).entity(str).build();
    }
}
