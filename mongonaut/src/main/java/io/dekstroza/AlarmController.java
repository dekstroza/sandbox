package io.dekstroza;

import io.micrometer.core.annotation.Timed;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.reactivex.Flowable;
import io.reactivex.Single;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Singleton
@Controller("/mongonaut")
public class AlarmController {

    private final AlarmService alarmService;

    @Inject
    public AlarmController(final AlarmService alarmService) {
        this.alarmService = alarmService;
    }

    /**
     * Get all alarms from database
     *
     * @return Returns json array of all alarms in the database
     */
    @Timed(value = "method.alarms.api.getall", percentiles = { 0.5, 0.95, 0.99 }, histogram = true, description = "Read all alarms api metric")
    @Get(value = "/alarms", produces = MediaType.APPLICATION_JSON)
    public Flowable<Alarm> getAll() {
        return alarmService.getAll();
    }

    /**
     * Save single alarm into the database
     *
     * @param id
     *            Unique identifier of the alarm
     * @param name
     *            Alarm name
     * @param severity
     *            Alarm severity
     * @return Persisted alarm as json object
     */
    @Timed(value = "method.alarms.api.save", percentiles = { 0.5, 0.95, 0.99 }, histogram = true, description = "Insert alarm api metric")
    @Post(value = "/alarms", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<Alarm>> save(@NotNull Integer id, @NotBlank String name, @NotBlank String severity) {
        return alarmService.save(id, name, severity).map(HttpResponse::created);
    }

}
