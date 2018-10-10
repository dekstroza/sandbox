package io.dekstroza.thorntail;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/health")
public interface HealthAPI {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHealth();
}
