package io.dekstroza;

import io.micronaut.context.annotation.Primary;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Primary
@Singleton
public class TestAlarmServiceImpl implements AlarmService {

    private static final Logger log = LoggerFactory.getLogger(TestAlarmServiceImpl.class);

    private final Map<Integer, Alarm> alarmsDb = new ConcurrentHashMap<>();

    @PostConstruct
    public void postConstruct() {
        alarmsDb.put(1, new Alarm(1, "Test Alarm", "CRITICAL"));
    }

    @Override
    public Flowable<Alarm> getAll() {
        return Flowable.fromIterable(alarmsDb.values());
    }

    @Override
    public Single<Alarm> save(Integer id, String name, String severity) {
        log.debug("Saving alarm Alarm[{},{},{}]", id, name, severity);
        Alarm alarm = new Alarm(id, name, severity);
        alarmsDb.put(id, alarm);
        log.debug("Returning Single.just(alarm)");
        return Single.just(alarm);
    }
}
