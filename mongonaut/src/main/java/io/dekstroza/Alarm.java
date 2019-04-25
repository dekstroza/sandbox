package io.dekstroza;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

@Schema(name = "Alarm", description = "Alarm representation")
@BsonDiscriminator
public class Alarm {

    private final Integer id;
    private final String name;
    private final String severity;

    @BsonCreator
    public Alarm(@BsonProperty("Id") Integer id, @BsonProperty("name") String name, @BsonProperty("severity") String severity) {
        this.id = id;
        this.name = name;
        this.severity = severity;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSeverity() {
        return severity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Alarm alarm = (Alarm) o;
        return getId().equals(alarm.getId()) && getName().equals(alarm.getName()) && getSeverity().equals(alarm.getSeverity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getSeverity());
    }
}
