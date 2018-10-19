package io.dekstroza.repository.cassandra.model;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Objects;
import java.util.UUID;

@Table(
    keyspace = "testspace",
    name = "testmodel",
    readConsistency = "QUORUM",
    writeConsistency = "QUORUM",
    caseSensitiveKeyspace = false,
    caseSensitiveTable = false)
public class TestCassandraModel {

  @PartitionKey
  @Column(name = "uuidField")
  private UUID uuidField;

  @Column(name = "stringField")
  private String stringField;

  @Column(name = "intField")
  private int intField;

  public UUID getUuidField() {
    return uuidField;
  }

  public void setUuidField(UUID uuidField) {
    this.uuidField = uuidField;
  }

  public String getStringField() {
    return stringField;
  }

  public void setStringField(String stringField) {
    this.stringField = stringField;
  }

  public int getIntField() {
    return intField;
  }

  public void setIntField(int intField) {
    this.intField = intField;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TestCassandraModel that = (TestCassandraModel) o;
    return getIntField() == that.getIntField()
        && Objects.equals(getUuidField(), that.getUuidField())
        && Objects.equals(getStringField(), that.getStringField());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUuidField(), getStringField(), getIntField());
  }
}
