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

  final Cluster cluster;
  final Logger logger = LoggerFactory.getLogger(CassandraCluster.class);
  Session session;

  CassandraCluster(String[] contactPoints, String clusterName, int port) {
    this.cluster =
        Cluster.builder()
            .withClusterName(clusterName)
            .addContactPoints(contactPoints)
            .withPort(port)
            .build();
    logger.trace(
        "Created Cassandra Cluster [{}] Configuration using contact points:{} and port {}",
        new Object[] {clusterName, contactPoints, port});
  }

  void shutdown() {
    if (session != null && !session.isClosed()) {
      session.close();
      logger.info("Session closed.");
    }
    if (!cluster.isClosed()) {
      cluster.close();
      logger.info("Cluster closed.");
    }
  }

  Session getSession() {
    return session;
  }

  void clusterConnect() {
    this.session = cluster.connect();
    logger.info("Connected to cluster {}", cluster.getClusterName());
  }

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
