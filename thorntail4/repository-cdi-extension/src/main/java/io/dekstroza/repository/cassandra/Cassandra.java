package io.dekstroza.repository.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.schemabuilder.Create;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.mapping.MappedProperty;
import com.datastax.driver.mapping.annotations.Table;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Cassandra {

    final Cluster cluster;
    final Logger logger = LoggerFactory.getLogger(Cassandra.class);
    Session session;

    public Cassandra(String[] contactPoints) {
        this.cluster = Cluster.builder().withClusterName("myCluster").addContactPoints(contactPoints).build();
        logger.trace("Created Cassandra Configuration from contact points:{}", contactPoints.toString());
    }

    public void shutdown() {
        if (session != null && !session.isClosed()) {
            session.close();
            logger.trace("Closed session.");
        }
        if (!cluster.isClosed()) {
            cluster.close();
            logger.trace("Closed cluster.");
        }
    }

    public Session getSession() {
        return session;
    }

    public void clusterConnect() {
        this.session = cluster.connect();
        logger.trace("Connected to cluster {}", cluster.getMetadata().toString());
    }

    public void createKeyspacesAndTables(Map<Table, Create> createMap) {

        final ImmutableMap<String, Object> replicationMap = ImmutableMap.of("class", (Object) "SimpleStrategy", "replication_factor", 1);
        createMap.forEach((table, create) -> {
            getSession().execute(SchemaBuilder.createKeyspace(table.keyspace()).ifNotExists().with().durableWrites(true).replication(replicationMap));
            getSession().execute(create);
        });
    }
}
