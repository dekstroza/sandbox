package io.dekstroza.resource;

import io.dekstroza.model.Game;
import io.dekstroza.repository.annotations.Repository;
import io.dekstroza.repository.api.CrudRepository;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static javax.ws.rs.core.Response.status;

@ApplicationScoped
@Transactional
@Path("/")
public class GameResource {

  @Inject Logger logger;
  @Repository private CrudRepository<Game, UUID> repository;

  @Metered(
      name = "getGame",
      reusable = true,
      tags = {"async=false", "path=/game/{id}", "method=GET"})
  @Path("/game/{id}")
  @GET
  @Produces(APPLICATION_JSON)
  public void getGameById(@PathParam("id") UUID id, @Suspended AsyncResponse response) {
    final Optional<Game> optionalGame = repository.findById(id);
    if (optionalGame.isPresent()) {
      response.resume(status(OK).entity(optionalGame.get()).build());
    } else {
      response.resume(status(NOT_FOUND).entity(format("Game with id:%s", id)).build());
    }
  }

  @Metered(
      name = "createGame",
      reusable = true,
      tags = {"async=false", "path=/game", "method=POST"})
  @Produces(APPLICATION_JSON)
  @Consumes(APPLICATION_JSON)
  @Path("/game")
  @POST
  public void createNewGame(
      @NotNull Game game, @Context UriInfo uriInfo, @Suspended AsyncResponse response) {
    try {
      game.setId(UUID.randomUUID());
      repository.create(game);
      response.resume(
          Response.created(
                  uriInfo.getBaseUriBuilder().path("game").path(game.getId().toString()).build())
              .entity(game)
              .build());
    } catch (Exception e) {
      logger.error("Error performing operation", e);
      response.resume(status(BAD_REQUEST).entity(e.getMessage()).build());
    }
  }

  @Metered(
      name = "updateGame",
      reusable = true,
      tags = {"async=false", "path=/game/{id}", "method=PUT"})
  @Produces(APPLICATION_JSON)
  @Consumes(APPLICATION_JSON)
  @Path("/game/{id}")
  @PUT
  public void updateGame(
      @PathParam("id") UUID id,
      @NotNull Game game,
      @Context UriInfo uriInfo,
      @Suspended AsyncResponse response) {
    try {
      game.setId(id);
      repository.update(game);
      response.resume(Response.ok(game).build());
    } catch (Exception e) {
      logger.error("Error performing operation", e);
      response.resume(status(BAD_REQUEST).entity(e.getMessage()).build());
    }
  }

  @Metered(
      name = "deleteGame",
      reusable = true,
      tags = {"async=false", "path=/game/{id}", "method=DELETE"})
  @Produces(APPLICATION_JSON)
  @Path("/game/{id}")
  @DELETE
  public void deleteGame(
      @PathParam("id") UUID id, @Context UriInfo uriInfo, @Suspended AsyncResponse response) {
    try {
      repository.delete(
          repository
              .findById(id)
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(format("Game with id:%s does not exist", id))));
      response.resume(Response.noContent().build());
    } catch (Exception e) {
      logger.error("Error performing operation", e);
      response.resume(status(BAD_REQUEST).entity(e.getMessage()).build());
    }
  }
}
