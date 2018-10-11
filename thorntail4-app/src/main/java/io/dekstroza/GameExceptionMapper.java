package io.dekstroza;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GameExceptionMapper implements ExceptionMapper<GameNotFoundException> {
    @Override
    public Response toResponse(GameNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    }
}
