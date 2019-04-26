package io.dekstroza;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micrometer.core.annotation.Timed;
import io.reactivex.Flowable;
import io.reactivex.Single;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AlarmServiceImpl implements AlarmService {

    private final MongoClient mongoClient;

    @Inject
    public AlarmServiceImpl(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    @Timed(value = "method.alarms.service.getall", percentiles = { 0.5, 0.95, 0.99 }, histogram = true, description = "Read all service call metric")
    public Flowable<Alarm> getAll() {
        return Flowable.fromPublisher(getCollection().find());
    }

    @Override
    @Timed(value = "method.alarms.service.save", percentiles = { 0.5, 0.95, 0.99 }, histogram = true, description = "Save alarm service call metric")
    public Single<Alarm> save(Integer id, String name, String severity) {
        Alarm alarm = new Alarm(id, name, severity);
        return Single.fromPublisher(getCollection().insertOne(alarm)).map(success -> alarm);
    }

    private MongoCollection<Alarm> getCollection() {
        return mongoClient.getDatabase("mongonaut").getCollection("alarms", Alarm.class);
    }
}
