package io.dekstroza.resource;

import io.dekstroza.model.Game;
import io.dekstroza.repository.annotations.Repository;
import io.dekstroza.repository.api.CrudRepository;
import io.thorntail.servlet.annotation.Primary;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static javax.ws.rs.core.Response.created;
import static javax.ws.rs.core.Response.status;

@ApplicationScoped
@Transactional
@Path("/async")
public class GameResourceAsync {

  @Inject @Primary URL url;

  @Inject Logger logger;

  @Repository private CrudRepository<Game, UUID> repository;

  @Metered(
      name = "getGame",
      reusable = true,
      tags = {"async=true", "path=/async/game/{id}", "method=GET"})
  @Path("/game/{id}")
  @GET
  @Produces(APPLICATION_JSON)
  public void getGameById(@PathParam("id") UUID id, @Suspended AsyncResponse response) {
    repository
        .findByIdAsync(id)
        .handle(
            (game, throwable) -> {
              return throwable == null
                  ? game.map(game1 -> response.resume(status(OK).entity(game1).build()))
                      .orElseGet(
                          () ->
                              response.resume(
                                  status(NOT_FOUND)
                                      .entity(format("Game with id:%s was not found", id))
                                      .build()))
                  : response.resume(status(BAD_REQUEST).entity(throwable.getMessage()).build());
            })
        .join();
  }

  @Metered(
      name = "createGame",
      reusable = true,
      tags = {"async=true", "path=/async/game", "method=POST"})
  @Produces(APPLICATION_JSON)
  @Consumes(APPLICATION_JSON)
  @Path("/game")
  @POST
  public void createNewGame(@NotNull Game game, @Suspended AsyncResponse response) {
    game.setId(randomUUID());
    repository
        .createAsync(game)
        .handle(
            (game1, throwable) ->
                throwable == null
                    ? response.resume(
                        created(
                                URI.create(
                                    url.toExternalForm()
                                        + "async/game/"
                                        + game1.getId().toString()))
                            .entity(game1)
                            .build())
                    : response.resume(status(BAD_REQUEST).entity(throwable.getMessage()).build()))
        .join();
  }

  @Metered(
      name = "updateGame",
      reusable = true,
      tags = {"async=true", "path=/async/game/{id}", "method=PUT"})
  @Produces(APPLICATION_JSON)
  @Consumes(APPLICATION_JSON)
  @Path("/game/{id}")
  @PUT
  public void updateGame(
      @PathParam("id") UUID id, @NotNull Game game, @Suspended AsyncResponse response) {
    game.setId(id);
    repository
        .updateAsync(game)
        .handle(
            (game1, throwable) ->
                throwable == null
                    ? response.resume(status(OK).entity(game).build())
                    : response.resume(status(BAD_REQUEST).entity(throwable.getMessage()).build()))
        .join();
  }

  @Metered(
      name = "deleteGame",
      reusable = true,
      tags = {"async=true", "path=/async/game/{id}", "method=DELETE"})
  @Produces(APPLICATION_JSON)
  @Path("/game/{id}")
  @DELETE
  public void deleteGame(@NotNull @PathParam("id") UUID id, @Suspended AsyncResponse response) {
    Game game = new Game();
    game.setId(id);
    repository
        .deleteAsync(game)
        .handle(
            (aVoid, throwable) ->
                throwable == null
                    ? response.resume(status(NO_CONTENT).build())
                    : response.resume(status(BAD_REQUEST).entity(throwable.getMessage()).build()))
        .join();
  }
}
