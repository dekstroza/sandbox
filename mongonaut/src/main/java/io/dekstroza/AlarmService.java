package io.dekstroza;

import io.reactivex.Flowable;
import io.reactivex.Single;

public interface AlarmService {

    Flowable<Alarm> getAll();

    Single<Alarm> save(Integer id, String name, String severity);
}
