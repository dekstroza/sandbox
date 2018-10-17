package io.dekstroza;

import com.sun.tools.javac.util.List;
import io.thorntail.Thorntail;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

@RunWith(JUnit4.class)
public class CassandraRepositoryTest {

  private static final Logger log = LoggerFactory.getLogger(CassandraRepositoryTest.class);
  private static final Game newGame;

  static {
    newGame = new Game();
    newGame.setTrack("Test Track");
    newGame.setBandName("Test Band");
    newGame.setBandMembers(List.of("Test 1", "Test 2", " Test 3"));
  }

  @BeforeClass
  public static void startCassandra() throws Exception {
    EmbeddedCassandraServerHelper.startEmbeddedCassandra();
    Thorntail.run();
  }

  @Test
  public void testGameRestTemplate() throws Exception {
    // CREATE-POST
    Game createdGame =
        given()
            .body(newGame)
            .and()
            .contentType(JSON)
            .when()
            .post("http://localhost:8080/game")
            .then()
            .statusCode(is(201))
            .and()
            .contentType(JSON)
            .and()
            .header("Location", not(isEmptyOrNullString()))
            .extract()
            .body()
            .as(Game.class);

    // READ-GET-BY-ID
    given()
        .pathParam("id", createdGame.getId())
        .and()
        .when()
        .get("http://localhost:8080/game/{id}")
        .then()
        .statusCode(is(200))
        .and()
        .contentType(JSON)
        .and()
        .extract()
        .body()
        .as(Game.class)
        .equals(createdGame);

    // UPDATE-PUT
    createdGame.setBandName("Put Band Name");
    given()
        .body(createdGame)
        .and()
        .contentType(JSON)
        .when()
        .put("http://localhost:8080/game")
        .then()
        .statusCode(is(200))
        .and()
        .contentType(JSON)
        .and()
        .body(containsString("Put Band Name"));

    // DELETE-DELETE
    given()
        .pathParam("id", createdGame.getId())
        .and()
        .contentType(JSON)
        .when()
        .delete("http://localhost:8080/game/{id}")
        .then()
        .statusCode(is(204));
  }

  @AfterClass
  public static void cleanup() throws Exception {
    Thorntail.current().stop();
    EmbeddedCassandraServerHelper.stopEmbeddedCassandra();
  }
}
