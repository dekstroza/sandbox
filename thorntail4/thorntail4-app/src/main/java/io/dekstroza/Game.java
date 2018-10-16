package io.dekstroza;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.UUID;

@Table(keyspace = "ks", name = "games", readConsistency = "QUORUM", writeConsistency = "QUORUM", caseSensitiveKeyspace = false, caseSensitiveTable = false)
public class Game {

    @PartitionKey
    @Column(name = "id")
    private UUID id;
    @Column(name = "bandName")
    private String bandName;
    @Column(name = "track")
    private String track;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }
}
