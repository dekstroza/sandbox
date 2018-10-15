package io.dekstroza;

import io.dekstroza.domain.Band;
import io.dekstroza.domain.Game;
import io.dekstroza.domain.Song;
import io.thorntail.test.EphemeralPorts;
import io.thorntail.test.ThorntailTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@EphemeralPorts
@RunWith(ThorntailTestRunner.class)
public class ServiceEndpointITest {

    @Inject
    private EmbeddedCassandra embeddedCassandra;

    @Test
    public void testCreateNewBand() throws Exception {
        Band band = new Band();
        band.setBandName("TestBand");
        band.setMembers("Test1,Test2,Test3");
        given().body(band).contentType(JSON).post("/band").then().contentType(JSON).and().statusCode(201);

    }

    @Test
    public void testGetBandById() throws Exception {
        Band band = new Band();
        band.setBandName("TestBand");
        band.setMembers("Test1,Test2,Test3");
        Band band1 = given().body(band).contentType(JSON).post("/band").then().contentType(JSON).and().statusCode(201).and().extract().body().as(
                   Band.class);
        given().pathParam("id", band1.getId()).get("/band/{id}").then().contentType(JSON).and().statusCode(200);

    }

    @Test
    public void testCreateNewSong() throws Exception {
        Song song = new Song();
        song.setName("Maybe you need me");
        song.setPerformer("Eminem");
        given().body(song).contentType(JSON).post("/song").then().contentType(JSON).and().statusCode(201);

    }

    @Test
    public void testGetSongById() throws Exception {
        Song song = new Song();
        song.setName("Maybe you need me");
        song.setPerformer("Eminem");
        Song result = given().body(song).contentType(JSON).post("/song").then().contentType(JSON).and().statusCode(201).and().extract().body().as(
                   Song.class);
        given().pathParam("id", result.getId()).get("/song/{id}").then().contentType(JSON).and().statusCode(200);

    }

    @Test
    public void testCreateNewGame() throws Exception {
        Song song = new Song();
        song.setName("Maybe you need me");
        song.setPerformer("Eminem");
        Song persistedSong = given().body(song).contentType(JSON).post("/song").then().contentType(JSON).and().statusCode(201).and().extract().body()
                   .as(Song.class);

        Band band = new Band();
        band.setBandName("TestBand");
        band.setMembers("Test1,Test2,Test3");
        Band persistedBand = given().body(band).contentType(JSON).post("/band").then().contentType(JSON).and().statusCode(201).and().extract().body()
                   .as(Band.class);

        Game game = new Game();
        game.setSong(persistedSong);
        game.setBand(persistedBand);
        game.setVotes(1000L);
        given().body(game).contentType(JSON).post("/game").then().contentType(JSON).and().statusCode(201);

    }

    @Test
    public void testGetGameById() throws Exception {
        Song song = new Song();
        song.setName("Maybe you need me");
        song.setPerformer("Eminem");
        Song persistedSong = given().body(song).contentType(JSON).post("/song").then().contentType(JSON).and().statusCode(201).and().extract().body()
                   .as(Song.class);

        Band band = new Band();
        band.setBandName("TestBand");
        band.setMembers("Test1,Test2,Test3");
        Band persistedBand = given().body(band).contentType(JSON).post("/band").then().contentType(JSON).and().statusCode(201).and().extract().body()
                   .as(Band.class);

        Game game = new Game();
        game.setSong(persistedSong);
        game.setBand(persistedBand);
        game.setVotes(1000L);
        Game result = given().body(game).contentType(JSON).post("/game").then().contentType(JSON).and().statusCode(201).and().extract().body().as(
                   Game.class);
        given().pathParam("id", result.getId()).get("/game/{id}").then().contentType(JSON).and().statusCode(200);

    }

}
