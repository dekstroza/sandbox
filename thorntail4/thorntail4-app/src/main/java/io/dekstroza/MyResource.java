package io.dekstroza;

import io.dekstroza.repository.api.CrudRepository;
import io.dekstroza.repository.cdi.annotations.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Path("/")
public class MyResource {

    private final Logger logger = LoggerFactory.getLogger(MyResource.class);
    
    @Repository
    private CrudRepository<Game, UUID> repository;

    @Path("/game/{id}")
    @GET
    @Produces(APPLICATION_JSON)
    public Response getGameById(@PathParam("id") UUID id) {
        try {
            Optional<Game> og = repository.findById(id);
            return og.isPresent() ? Response.ok(og.get()).build() : Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Path("/game")
    @POST
    public void createNewGame(@NotNull Game game, @Context UriInfo uriInfo, @Suspended AsyncResponse response) {
        try {
            game.setId(UUID.randomUUID());
            repository.create(game);
            response.resume(Response.created(uriInfo.getBaseUriBuilder().path("game").path(game.getId().toString()).build()).entity(game).build());
        } catch (Exception e) {
            e.printStackTrace();
            response.resume(Response.status(BAD_REQUEST).entity(e.getMessage()).build());
        }

    }
}

