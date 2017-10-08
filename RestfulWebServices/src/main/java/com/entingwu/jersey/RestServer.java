package com.entingwu.jersey;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/")
public class RestServer {
    
    @GET
    @Path("/{myvert}/{skierID}&{dayNum}")
    public Response getData(
            @PathParam("myvert") String myvert,
            @PathParam("skierID") int skierID,
            @PathParam("dayNum") int dayNum) {
        String vertical = myvert;
        int liftNum = dayNum;
        String result = new StringBuilder()
                .append(vertical)
                .append(",")
                .append(liftNum)
                .toString();
        return Response.status(200).entity(result).build();
    }
    
    @POST
    @Path("/load/{resortID}&{dayNum}&{timestamp}&{skierID}&{liftID}")
    public Response postData(
            @PathParam("resortID") int resortID,
            @PathParam("dayNum") int dayNum,
            @PathParam("timestamp") int timestamp,
            @PathParam("skierID") int skierID,
            @PathParam("liftID") int liftID) {
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
