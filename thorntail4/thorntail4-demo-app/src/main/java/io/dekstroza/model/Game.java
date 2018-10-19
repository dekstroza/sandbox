package io.dekstroza.model;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Table(
    keyspace = "ks1",
    name = "games1",
    readConsistency = "QUORUM",
    writeConsistency = "QUORUM",
    caseSensitiveKeyspace = false,
    caseSensitiveTable = false)
public class Game {

  @PartitionKey
  @Column(name = "id")
  private UUID id;

  @Column(name = "bandName")
  private String bandName;

  @Column(name = "track")
  private String track;

  @Column(name = "bandMembers")
  private List<String> bandMembers;

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

  public List<String> getBandMembers() {
    return bandMembers;
  }

  public void setBandMembers(List<String> bandMembers) {
    this.bandMembers = bandMembers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Game game = (Game) o;
    return Objects.equals(getId(), game.getId())
        && Objects.equals(getBandName(), game.getBandName())
        && Objects.equals(getTrack(), game.getTrack())
        && Objects.equals(getBandMembers(), game.getBandMembers());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getBandName(), getTrack(), getBandMembers());
  }
}
