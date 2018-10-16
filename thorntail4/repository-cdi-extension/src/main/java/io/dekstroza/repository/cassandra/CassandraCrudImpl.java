package io.dekstroza.repository.cassandra;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import io.dekstroza.repository.api.CrudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.Optional;

class CassandraCrudImpl<X, I> implements CrudRepository<X, I> {

    MappingManager mappingManager;
    Class<X> xClass;
    Class<I> iClass;
    private final Logger logger = LoggerFactory.getLogger(CassandraCrudImpl.class);

    public CassandraCrudImpl(MappingManager mappingManager, Class<X> x, Class<I> i) {
        logger.trace("Creating CassandraCrud Bean for type {} with id type {}.", x.getCanonicalName(), i.getCanonicalName());
        this.mappingManager = mappingManager;
        this.xClass = x;
        this.iClass = i;
    }

    public Optional<X> findById(@NotNull I id) {
        try {
            return Optional.ofNullable(mappingManager.mapper(xClass).get(id));
        } catch (Exception e) {
            logger.error(String.format("Error looking up entity with id: %s", id), e);
            return Optional.empty();
        }
    }

    public X create(X entity) {
        mappingManager.mapper(xClass).save(entity, Mapper.Option.ifNotExists(true));
        return entity;
    }

    public X update(X entity) {
        mappingManager.mapper(xClass).save(entity);
        return entity;
    }

    public void delete(X entity) {
        mappingManager.mapper(xClass).delete(entity);
    }

}
