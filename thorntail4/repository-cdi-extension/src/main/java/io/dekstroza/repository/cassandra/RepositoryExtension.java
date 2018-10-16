package io.dekstroza.repository.cassandra;

import com.datastax.driver.core.schemabuilder.Create;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import io.dekstroza.repository.annotations.CassandraConfig;
import io.dekstroza.repository.annotations.Repository;
import io.dekstroza.repository.api.CrudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static io.dekstroza.repository.cassandra.TypeMappings.resolveType;

class RepositoryExtension<T> implements Extension {

    CassandraCluster cassandraCluster;
    Map<Table, Create> createOnStartupCQL = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(RepositoryExtension.class);

    public void cassandraConfig(@Observes @WithAnnotations(CassandraConfig.class) ProcessAnnotatedType pat) {
        final AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        CassandraConfig cassandraConfig = annotatedType.getAnnotation(CassandraConfig.class);
        if (cassandraConfig != null && cassandraCluster == null) {
            this.cassandraCluster = new CassandraCluster(cassandraConfig.contact_points());
        }
    }

    public void cassandraSchemaBuilder(@Observes @WithAnnotations(Table.class) ProcessAnnotatedType pat) {
        final AnnotatedType<T> annotatedType = pat.getAnnotatedType();
        Table table = annotatedType.getAnnotation(Table.class);
        if (table != null) {
            Create createTable = SchemaBuilder.createTable(table.keyspace(), table.name()).ifNotExists();
            annotatedType.getFields().forEach(annotatedField -> {
                PartitionKey partitionKeyAnnotation = annotatedField.getAnnotation(PartitionKey.class);
                if (partitionKeyAnnotation != null) {
                    createTable.addPartitionKey(annotatedField.getJavaMember().getName(), resolveType(annotatedField));
                }
                Column columnAnnotation = annotatedField.getAnnotation(Column.class);
                if (columnAnnotation != null && partitionKeyAnnotation == null) {
                    createTable.addColumn(annotatedField.getJavaMember().getName(), resolveType(annotatedField));
                }
            });
            createOnStartupCQL.put(table, createTable);
        }
    }

    public <T> void processInjectionTarget(@Observes ProcessInjectionTarget<T> pit) {
        final InjectionTarget<T> it = pit.getInjectionTarget();
        final AnnotatedType<T> at = pit.getAnnotatedType();
        final InjectionTarget<T> wrapped = new InjectionTarget<T>() {
            @Override
            public void inject(T instance, CreationalContext<T> ctx) {
                it.inject(instance, ctx);
                Arrays.asList(at.getJavaClass().getDeclaredFields()).forEach(field -> {
                    final Repository annotation = field.getAnnotation(Repository.class);
                    if (annotation != null) {
                        if (field.getType().isAssignableFrom(CrudRepository.class)) {
                            field.setAccessible(Boolean.TRUE);
                            if (field.getGenericType() instanceof ParameterizedType) {
                                logger.trace("Injecting Repository annotation into field {}", field.toGenericString());
                                final ParameterizedType pt = (ParameterizedType) field.getGenericType();
                                try {
                                    field.set(instance,
                                               createCrudRepository(new MappingManager(cassandraCluster.getSession()), pt.getActualTypeArguments()[0],
                                                          pt.getActualTypeArguments()[1]));
                                } catch (IllegalArgumentException | IllegalAccessException | ClassNotFoundException e) {
                                    logger.error("Could not inject repository", e);
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void postConstruct(T instance) {
                it.postConstruct(instance);
            }

            @Override
            public void preDestroy(T instance) {
                it.preDestroy(instance);
            }

            @Override
            public void dispose(T instance) {
                it.dispose(instance);
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                return it.getInjectionPoints();
            }

            @Override
            public T produce(CreationalContext<T> ctx) {
                return it.produce(ctx);
            }
        };
        pit.setInjectionTarget(wrapped);
    }

    public void afterDeploymentValidation(@Observes AfterDeploymentValidation adv, final BeanManager bm) {
        try {
            cassandraCluster.clusterConnect();
            cassandraCluster.createKeyspacesAndTables(createOnStartupCQL);
        } catch (Exception e) {
            adv.addDeploymentProblem(e);
        }
    }

    CrudRepository createCrudRepository(MappingManager mappingManager, Type entityType, Type idType) throws ClassNotFoundException {
        return new CassandraCrudImpl(mappingManager, Class.forName(entityType.getTypeName()), Class.forName(idType.getTypeName()));
    }

    public void handleShutdown(@Observes final BeforeShutdown beforeShutdown) {
        logger.trace("Before shutdown called.");
        cassandraCluster.shutdown();
    }
}