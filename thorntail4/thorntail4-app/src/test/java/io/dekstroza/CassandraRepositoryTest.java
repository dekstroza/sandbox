package io.dekstroza;

import com.google.common.collect.ImmutableList;
import io.dekstroza.model.Game;
import io.thorntail.Thorntail;
import org.assertj.core.api.Assertions;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

@RunWith(JUnit4.class)
public class CassandraRepositoryTest {

  private static final Logger log = LoggerFactory.getLogger(CassandraRepositoryTest.class);
  private Game newGame;

  @BeforeClass
  public static void startCassandra() {
    try {
      EmbeddedCassandraServerHelper.startEmbeddedCassandra();
    } catch (Exception e) {
    }
    try {
      Thorntail.run();
    } catch (Exception e) {
    }
  }

  @Before
  public void setGameEntity() {
    newGame = new Game();
    newGame.setId(UUID.randomUUID());
    newGame.setTrack("Test Track");
    newGame.setBandName("Test Band");
    newGame.setBandMembers(ImmutableList.of("Test 1", "Test 2", " Test 3"));
  }

  @Test
  public void testGameRestTemplate() {
    // CREATE-POST
    Game createdGame = assertGameCreate("http://localhost:8080/game");
    // READ-GET
    assertGameRead(createdGame, "http://localhost:8080/game/{id}");
    // UPDATE-PUT
    assertGameUpdate(createdGame, "http://localhost:8080/game/{id}");
    // DELETE-DELETE
    assertGameDelete(createdGame.getId(), "http://localhost:8080/game/{id}");
  }

  @Test
  public void testGameRestTemplateAsync() {
    // CREATE-POST
    Game createdGame = assertGameCreate("http://localhost:8080/async/game");
    // READ-GET
    assertGameRead(createdGame, "http://localhost:8080/async/game/{id}");
    // UPDATE-PUT
    assertGameUpdate(createdGame, "http://localhost:8080/async/game/{id}");
    // DELETE-DELETE
    assertGameDelete(createdGame.getId(), "http://localhost:8080/async/game/{id}");
  }

  private Game assertGameCreate(String url) {
    return given()
        .body(newGame)
        .and()
        .contentType(JSON)
        .when()
        .post(url)
        .then()
        .statusCode(is(201))
        .and()
        .contentType(JSON)
        .and()
        .header("Location", not(isEmptyOrNullString()))
        .extract()
        .body()
        .as(Game.class);
  }

  private void assertGameRead(Game gameExpected, String url) {
    final Game gameRead =
        given()
            .pathParam("id", gameExpected.getId())
            .and()
            .when()
            .get(url)
            .then()
            .statusCode(is(200))
            .and()
            .contentType(JSON)
            .and()
            .extract()
            .body()
            .as(Game.class);
    Assertions.assertThat(gameExpected).isEqualTo(gameRead);
  }

  private void assertGameUpdate(Game game, String url) {
    game.setBandName("Put Band Name");
    given()
        .pathParam("id", game.getId())
        .body(game)
        .and()
        .contentType(JSON)
        .when()
        .put(url)
        .then()
        .statusCode(is(200))
        .and()
        .contentType(JSON)
        .and()
        .body(containsString("Put Band Name"));
  }

  private void assertGameDelete(UUID id, String url) {
    given()
        .pathParam("id", id)
        .and()
        .contentType(JSON)
        .when()
        .delete(url)
        .then()
        .statusCode(is(204));
  }

  @AfterClass
  public static void cleanup() {
    try {
      Thorntail.current().stop();
    } catch (Exception e) {
    }
    try {
      EmbeddedCassandraServerHelper.cleanDataEmbeddedCassandra("ks1");
    } catch (Exception e) {
    }
  }
}
