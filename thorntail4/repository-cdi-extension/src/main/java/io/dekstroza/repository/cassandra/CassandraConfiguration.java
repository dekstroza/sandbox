package io.dekstroza.repository.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Table;
import com.google.common.collect.ImmutableMap;
import io.dekstroza.repository.cdi.annotations.CassandraConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Set;

public class CassandraConfiguration {

    final Cluster cluster;
    final Logger logger = LoggerFactory.getLogger(CassandraConfiguration.class);
    Session session;

    public CassandraConfiguration(String[] contactPoints) {
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

    public void createKeyspacesAndTables(Set<Table> tableSet) {
        tableSet.forEach(table -> {
            HashMap<String, Object> replicationOptions = new HashMap<>();
            replicationOptions.put("class", "SimpleStrategy");
            replicationOptions.put("replication_factor", "3");
            // create keyspace
            getSession().execute(SchemaBuilder.createKeyspace(table.keyspace()).ifNotExists().with().durableWrites(true)
                       .replication(ImmutableMap.of(
                                  "class", (Object) "SimpleStrategy",
                                  "replication_factor", 3
                                  )));
            // create table
            getSession().execute(SchemaBuilder.createTable(table.keyspace(), table.name())
                       .addPartitionKey("id", DataType.uuid())
                       .addColumn("bandName", DataType.varchar())
                       .addColumn("track", DataType.varchar())
                       .ifNotExists().withOptions().setConsistencyLevel(
                       ConsistencyLevel.QUORUM).toString());
        });
    }
}
