package io.dekstroza;

import io.dekstroza.domain.Game;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.created;
import static javax.ws.rs.core.Response.status;

@Path("/")
@ApplicationScoped
@Transactional
public class GameResource {

    @PersistenceContext(unitName = "CouchDB")
    private EntityManager entityManager;

    @GET
    @Produces(TEXT_PLAIN)
    public String hello() {
        return "Hello World";
    }

    @Metered
    @Counted
    @GET
    @Produces(APPLICATION_JSON)
    @Path("/{id}")
    public Response getGameById(@PathParam("id") Long id) {
        return status(OK).entity(ofNullable(entityManager.find(Game.class, id))
                   .orElseThrow(() -> new GameNotFoundException(String.format("Game with id %s was not found.", id)))).build();
    }

    @Metered
    @Counted
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    @Path("/")
    @POST
    public Response createNewGame(Game game, @Context UriInfo uriInfo) {
        try {
            entityManager.persist(game);
            return created(uriInfo.getAbsolutePathBuilder().path(game.getId().toString()).build()).entity(game).build();
        } catch (Exception e) {
            return status(BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}

