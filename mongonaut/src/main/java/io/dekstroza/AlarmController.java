package io.dekstroza;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Controller("/mongonaut")
public class AlarmController {

    private final MongoClient mongoClient;

    public AlarmController(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Get(value = "/alarms")
    public Flowable<Alarm> sayHelloWorld() {
        return Flowable.fromPublisher(getCollection().find());

    }

    @Post("/alarms")
    public Single<Alarm> save(Integer id, String name, String severity) {
        Alarm alarm = new Alarm(id, name, severity);
        return Single.fromPublisher(getCollection().insertOne(alarm)).map(success -> alarm);
    }

    private MongoCollection<Alarm> getCollection() {
        return mongoClient.getDatabase("mongonaut").getCollection("alarms", Alarm.class);
    }
}
