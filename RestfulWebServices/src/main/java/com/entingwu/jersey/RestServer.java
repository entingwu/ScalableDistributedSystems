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
    
    private Map<String, RFIDLiftData> map = new HashMap<>();
    
    @GET
    @Path("/{myvert}/{skierID}&{dayNum}")
    @Consumes(value = "text/plain")
    public Response getData(
            @PathParam("myvert") String myvert,
            @PathParam("skierID") String skierID,
            @PathParam("dayNum") String dayNum) {
        String vertical = myvert;
        String liftNum = "3";
        for (String key : map.keySet()) {
            System.out.println("server: " + map.get(key).toString());
        }
        if (map.containsKey(skierID)) {
            RFIDLiftData data = map.get(skierID);
            System.out.println("get server: " + data.toString());
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
            System.out.println("post server: " + map.get(skierID).toString());
            //post server: resortID = 0, dayNum = 3, skierID = 6, liftID = 9, time2017
            //int resortID, int dayNum, int skierID, int liftID, int time)
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
