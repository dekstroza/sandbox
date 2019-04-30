package io.dekstroza;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public interface AlarmService {

    Flowable<Alarm> getAll();

    Single<Alarm> save(Alarm alarm);

    Maybe<Alarm> findById(Integer id);

    Flowable<Alarm> findAlarmsBySeverity(String severity);
}
