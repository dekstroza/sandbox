package io.dekstroza;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.micrometer.core.annotation.Timed;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.mongodb.client.model.Filters.eq;

@Singleton
public class AlarmServiceImpl implements AlarmService {

    private final MongoClient mongoClient;

    @Inject
    public AlarmServiceImpl(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    @Timed(value = "method.alarms.service.getall", percentiles = { 0.5, 0.95, 0.99 }, description = "Read all service call metric")
    public Flowable<Alarm> getAll() {
        return Flowable.fromPublisher(getCollection().find());
    }

    @Override
    @Timed(value = "method.alarms.service.save", percentiles = { 0.5, 0.95, 0.99 }, description = "Save alarm service call metric")
    public Single<Alarm> save(Alarm alarm) {
        return Single.fromPublisher(getCollection().insertOne(alarm)).map(success -> alarm);
    }

    @Override
    @Timed(value = "method.alarms.service.findById", percentiles = { 0.5, 0.95, 0.99 }, description = "Find alarm by id service call metric")
    public Maybe<Alarm> findById(Integer id) {
        return Flowable.fromPublisher(getCollection().find(eq("_id", id), Alarm.class).first()).singleElement();
    }

    @Override
    @Timed(value = "method.alarms.service.findBySeverity", percentiles = { 0.5, 0.95,
               0.99 }, description = "Find alarms by severity service call metric")
    public Flowable<Alarm> findAlarmsBySeverity(String severity) {
        return Flowable.fromPublisher(getCollection().find(eq("severity", severity), Alarm.class));
    }

    private MongoCollection<Alarm> getCollection() {
        return mongoClient.getDatabase("mongonaut").getCollection("alarms", Alarm.class);
    }
}
