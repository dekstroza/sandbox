package io.dekstroza;

import io.dekstroza.domain.Band;
import io.dekstroza.domain.BaseEntity;
import io.dekstroza.domain.Game;
import io.dekstroza.domain.Song;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static javax.ws.rs.core.Response.status;

@Path("/")
@Transactional
public class ServiceEndpoint {

    @PersistenceContext(unitName = "cdb")
    EntityManager entityManager;

    @Counted(tags = { "method=POST", "path=/songs" }, reusable = true, name = "songs_endpoint_count")
    @Metered(tags = { "method=POST", "path=/songs" }, reusable = true, name = "songs_endpoint_meter")
    @Path("/song")
    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public void createNewSong(Song song, @Suspended AsyncResponse response, @Context UriInfo uriInfo) {
        createNewObject(response, song, uriInfo);
    }

    @Counted(tags = { "method=GET", "path=/songs/{id}" }, reusable = true, name = "songs_endpoint_count")
    @Metered(tags = { "method=GET", "path=/songs/{id}" }, reusable = true, name = "songs_endpoint_meter")
    @Path("/song/{id}")
    @GET
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public void getSongById(@PathParam("id") String id, @Suspended AsyncResponse response, @Context UriInfo uriInfo) {
        getById(id, Song.class, response);
    }

    @Counted(tags = { "method=POST", "path=/bands" }, reusable = true, name = "bands_endpoint_count")
    @Metered(tags = { "method=POST", "path=/bands" }, reusable = true, name = "bands_endpoint_meter")
    @Path("/band")
    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public void createNewBand(Band band, @Suspended AsyncResponse response, @Context UriInfo uriInfo) {
        createNewObject(response, band, uriInfo);
    }

    @Counted(tags = { "method=GET", "path=/bands/{id}" }, reusable = true, name = "bands_endpoint_count")
    @Metered(tags = { "method=GET", "path=/bands/{id}" }, reusable = true, name = "bands_endpoint_meter")
    @Path("/band/{id}")
    @GET
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public void getBandById(@PathParam("id") String id, @Suspended AsyncResponse response, @Context UriInfo uriInfo) {
        getById(id, Band.class, response);
    }

    @Counted(tags = { "method=GET", "path=/games/{id}" }, reusable = true, name = "games_endpoint_count")
    @Metered(tags = { "method=GET", "path=/games/{id}" }, reusable = true, name = "games_endpoint_meter")
    @Path("/game")
    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public void createNewGame(Game game, @Suspended AsyncResponse response, @Context UriInfo uriInfo) {
        createNewObject(response, game, uriInfo);
    }

    @Counted(tags = { "method=GET", "path=/games/{id}" }, reusable = true, name = "games_endpoint_count")
    @Metered(tags = { "method=GET", "path=/games/{id}" }, reusable = true, name = "games_endpoint_meter")
    @Path("/game/{id}")
    @GET
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public void getGameById(@PathParam("id") Long id, @Suspended AsyncResponse response, @Context UriInfo uriInfo) {
        getById(id, Game.class, response);
    }

    protected <T, R> boolean getById(R id, Class<T> clazz, AsyncResponse response) {
        T t = ofNullable(entityManager.find(clazz, id)).orElseThrow(() -> new EntityNotFoundException("Could not find requested object."));
        return response.resume(status(OK).entity(t).build());
    }

    protected <T extends BaseEntity, R> void createNewObject(AsyncResponse response, T t, UriInfo uriInfo) {
        try {
            entityManager.persist(t);
            response.resume(status(CREATED).contentLocation(uriInfo.getBaseUriBuilder().path(t.getClass().getSimpleName().toLowerCase())
                       .path(t.getId().toString()).build()).entity(t).build());
        } catch (Exception e) {
            e.printStackTrace();
            response.resume(status(BAD_REQUEST).entity(e.getMessage()).build());
        }
    }

}

