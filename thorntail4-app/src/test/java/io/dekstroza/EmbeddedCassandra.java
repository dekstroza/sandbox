package io.dekstroza;

import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class CassandraInjection {

    @Inject
    Logger logger;

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        try {
            EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        } catch (Exception e) {
        }
    }

    public void preDestroy(@Observes @Destroyed(ApplicationScoped.class) Object init) {
        try {
            EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
        } catch (Exception e) {
        }
    }
}
