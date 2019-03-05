package io.dekstroza;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/mongonaut")
public class ConferenceController {

    //private final MongoClient mongoClient;

    //public ConferenceController(MongoClient mongoClient) {
    // this.mongoClient =mongoClient;
    //}

    //    @Get(value = "/conferences")
    //    public Flowable<Conference> sayHelloWorld() {
    //        return Flowable.fromPublisher(getCollection().find());
    //
    //    }
    //
    //    @Post("/conferences")
    //    public Single<Conference> save(Integer id, String name) {
    //        Conference conference = new Conference(id, name);
    //        return Single.fromPublisher(getCollection().insertOne(conference)).map(success -> conference);
    //    }
    //
    //    private MongoCollection<Conference> getCollection() {
    //        return mongoClient.getDatabase("mongonaut").getCollection("conferences", Conference.class);
    //    }

    @Get(value = "/conferences")
    public String sayHelloWorld() {
        return "Hello World";
    }
}
