package io.dekstroza;

import io.dekstroza.domain.Band;
import io.dekstroza.domain.Game;
import io.dekstroza.domain.Song;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.status;

@Path("/")
@Transactional
public class MyResource {

    @PersistenceContext(unitName = "cassandraDB")
    private EntityManager entityManager;

    @Path("/songs")
    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public void createNewSong(Song song, @Suspended AsyncResponse response, @Context UriInfo uriInfo) {
        entityManager.persist(song);
        response.resume(status(CREATED).contentLocation(uriInfo.getBaseUriBuilder().path(song.getId()).build()).entity(song).build());
    }

    @Path("/songs/{id}")
    @GET
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public void getSongById(@PathParam("id") String id, @Suspended AsyncResponse response, @Context UriInfo uriInfo) {
        Optional.ofNullable(entityManager.find(Song.class, id)).ifPresent(song -> response.resume(status(OK).entity(song).build()));
    }

    @Path("/bands")
    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public void createNewBand(Band band, @Suspended AsyncResponse response, @Context UriInfo uriInfo) {
        entityManager.persist(band);
        response.resume(status(CREATED).contentLocation(uriInfo.getBaseUriBuilder().path(band.getId()).build()).entity(band).build());
    }

    @Path("/bands/{id}")
    @GET
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public void getBandById(@PathParam("id") String id, @Suspended AsyncResponse response, @Context UriInfo uriInfo) {
        Optional.ofNullable(entityManager.find(Band.class, id)).ifPresent(band -> response.resume(status(OK).entity(band).build()));
    }

    @Path("/games")
    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public void createNewGame(Game game, @Suspended AsyncResponse response, @Context UriInfo uriInfo) {
        entityManager.persist(game);
        response.resume(status(CREATED).contentLocation(uriInfo.getBaseUriBuilder().path(game.getId().toString()).build()).entity(game).build());
    }

    @Path("/games/{id}")
    @GET
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    public void getGameById(@PathParam("id") Long id, @Suspended AsyncResponse response, @Context UriInfo uriInfo) {
        Optional.ofNullable(entityManager.find(Game.class, id)).ifPresent(game -> response.resume(status(OK).entity(game).build()));
    }

}

