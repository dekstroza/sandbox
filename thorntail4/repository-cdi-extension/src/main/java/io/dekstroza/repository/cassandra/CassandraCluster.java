package io.dekstroza.repository.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.schemabuilder.Create;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.mapping.annotations.Table;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

class CassandraCluster {

  private final Cluster cluster;
  private final Logger logger = LoggerFactory.getLogger(CassandraCluster.class);
  private Session session;

  CassandraCluster(String[] contactPoints, String clusterName, int port) {
    this.cluster =
        Cluster.builder()
            .withClusterName(clusterName)
            .addContactPoints(contactPoints)
            .withPort(port)
            .build();
    logger.info(
        "Created Cassandra Cluster [{}] Configuration using contact points:{} and port {}",
        clusterName,
        contactPoints,
        port);
  }

  void shutdown() {
    if (session != null && !session.isClosed()) {
      session.close();
      logger.info("Session closed.");
    }
    if (!cluster.isClosed()) {
      cluster.close();
      logger.info("Cluster disconnected.");
    }
  }

  Session getSession() {
    return session;
  }

  void clusterConnect() {
    this.session = cluster.connect();
    logger.info("Connected to cluster {}", cluster.getClusterName());
  }

  // TODO: Provide annotation paramters to specify creation options
  void createKeyspacesAndTables(Map<Table, Create> createMap) {
    final ImmutableMap<String, Object> replicationMap =
        ImmutableMap.of("class", "SimpleStrategy", "replication_factor", 1);
    createMap.forEach(
        (table, create) -> {
          getSession()
              .execute(
                  SchemaBuilder.createKeyspace(table.keyspace())
                      .ifNotExists()
                      .with()
                      .durableWrites(true)
                      .replication(replicationMap));
          getSession().execute(create);
        });
  }
}
