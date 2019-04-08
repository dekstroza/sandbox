package io.dekstroza;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micrometer.core.annotation.Timed;
import io.micronaut.configuration.metrics.annotation.RequiresMetrics;
import io.micronaut.configuration.metrics.micrometer.annotation.MircometerTimed;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.reactivex.Flowable;
import io.reactivex.Single;

import javax.inject.Singleton;

@Singleton
@Controller("/mongonaut")
public class AlarmController {

    private final MongoClient mongoClient;

    public AlarmController(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Timed(value = "method.alarms.getall",percentiles = {0.5, 0.95},histogram = true, description = "Read all alarms timer")
    @Get(value = "/alarms")
    public Flowable<Alarm> getAll() {
        return Flowable.fromPublisher(getCollection().find());

    }

    @Timed(value = "method.alarms.save", percentiles = {0.5, 0.95},histogram = true, description = "Save alarm timer")
    @Post("/alarms")
    public Single<Alarm> save(Integer id, String name, String severity) {
        Alarm alarm = new Alarm(id, name, severity);
        return Single.fromPublisher(getCollection().insertOne(alarm)).map(success -> alarm);
    }

    private MongoCollection<Alarm> getCollection() {
        return mongoClient.getDatabase("mongonaut").getCollection("alarms", Alarm.class);
    }
}
