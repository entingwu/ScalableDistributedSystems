package com.entingwu.jersey;

import com.entingwu.jersey.model.RFIDLiftData;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
public class RestServer {
    
    private Map<Integer, RFIDLiftData> map = new HashMap<>();
    
    @GET
    @Path("/{myvert}/{skierID}&{dayNum}")
    public Response getData(
            @PathParam("myvert") String myvert,
            @PathParam("skierID") int skierID,
            @PathParam("dayNum") int dayNum) {
        String vertical = myvert;
        int liftNum = 0;
        if (map.containsKey(skierID)) {
            RFIDLiftData data = map.get(skierID);
            liftNum = data.getLiftID();
        }
        String result = new StringBuilder()
                .append(vertical)
                .append(",")
                .append(liftNum)
                .toString();
        return Response.status(200).entity(result).build();
    }
    
    @POST
    @Path("/load/{resortID}&{dayNum}&{timestamp}&{skierID}&{liftID}")
    @Produces(value = "text/plain")
    public String postData(
            @PathParam("resortID") String resortID,
            @PathParam("dayNum") String dayNum,
            @PathParam("timestamp") String timestamp,
            @PathParam("skierID") String skierID,
            @PathParam("liftID") String liftID) {
//        RFIDLiftData data = new RFIDLiftData(
//                resortID, dayNum, timestamp, skierID, liftID);
//        if (!map.containsKey(skierID)) {
//            map.put(skierID, data);
//        }
        String str = new StringBuilder()
                .append(resortID)
                .append(dayNum)
                .append(timestamp)
                .append(skierID)
                .append(liftID)
                .toString();
        System.out.println("sa bi" + str);
        return str;
        //return Response.status(200).entity(str).build();
    }
}
