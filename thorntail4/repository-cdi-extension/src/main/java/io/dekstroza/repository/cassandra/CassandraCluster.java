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

    CassandraCluster(String[] contactPoints) {
        this.cluster = Cluster.builder().withClusterName("myCluster").addContactPoints(contactPoints).build();
        logger.trace("Created Cassandra Configuration from contact points:{}", new Object[] { contactPoints });
    }

    void shutdown() {
        if (session != null && !session.isClosed()) {
            session.close();
            logger.trace("Session closed.");
        }
        if (!cluster.isClosed()) {
            cluster.close();
            logger.trace("Cluster closed.");
        }
    }

    Session getSession() {
        return session;
    }

    void clusterConnect() {
        this.session = cluster.connect();
        logger.trace("Connected to cluster {}", cluster.getClusterName());
    }

    void createKeyspacesAndTables(Map<Table, Create> createMap) {
        final ImmutableMap<String, Object> replicationMap = ImmutableMap.of("class", "SimpleStrategy", "replication_factor", 1);
        createMap.forEach((table, create) -> {
            getSession().execute(SchemaBuilder.createKeyspace(table.keyspace()).ifNotExists().with().durableWrites(true).replication(replicationMap));
            getSession().execute(create);
        });
    }
}
