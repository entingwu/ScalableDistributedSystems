package com.entingwu.jersey;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("server")
public class RestServer {
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getStatus() {
        return "alive";
    }
    
    @POST
    public Response postText(@FormParam("content") String content) {
        return Response.status(200).entity(content).build();
    }
}
