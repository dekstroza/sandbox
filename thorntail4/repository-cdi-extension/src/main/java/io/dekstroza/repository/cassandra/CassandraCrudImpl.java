package io.dekstroza.repository.cassandra;

import com.datastax.driver.mapping.MappingManager;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.dekstroza.repository.api.CrudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.datastax.driver.mapping.Mapper.Option.ifNotExists;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

class CassandraCrudImpl<X, I> implements CrudRepository<X, I> {

  private final Logger logger = LoggerFactory.getLogger(CassandraCrudImpl.class);
  private MappingManager mappingManager;
  private Class<X> xClass;
  private Class<I> iClass;

  public CassandraCrudImpl(MappingManager mappingManager, Class<X> x, Class<I> i) {
    logger.trace(
        "Creating CassandraCrud Bean for type {} with id type {}.",
        x.getCanonicalName(),
        i.getCanonicalName());
    this.mappingManager = mappingManager;
    this.xClass = x;
    this.iClass = i;
  }

  public Optional<X> findById(@NotNull I id) {
    try {
      return ofNullable(mappingManager.mapper(xClass).get(id));
    } catch (Exception e) {
      logger.error(format("Error looking up entity with id: %s", id), e);
      return Optional.empty();
    }
  }

  public CompletableFuture<Optional<X>> findByIdAsync(@NotNull I id) {
    return buildCompletableFuture(mappingManager.mapper(xClass).getAsync(id))
        .thenApply(Optional::ofNullable);
  }

  public X create(X entity) {
    mappingManager.mapper(xClass).save(entity, ifNotExists(true));
    return entity;
  }

  public CompletableFuture<X> createAsync(X entity) {
    return buildCompletableFuture(
            mappingManager.mapper(xClass).saveAsync(entity, ifNotExists(true)))
        .thenApply(aVoid -> entity);
  }

  public X update(X entity) {
    mappingManager.mapper(xClass).saveAsync(entity);
    return entity;
  }

  public CompletableFuture<X> updateAsync(X entity) {
    return buildCompletableFuture(mappingManager.mapper(xClass).saveAsync(entity))
        .thenApply(aVoid -> entity);
  }

  public void delete(X entity) {
    mappingManager.mapper(xClass).delete(entity);
  }

  public CompletableFuture<Void> deleteAsync(X entity) {
    return buildCompletableFuture(mappingManager.mapper(xClass).deleteAsync(entity));
  }

  private static <T> CompletableFuture<T> buildCompletableFuture(
      final ListenableFuture<T> listenableFuture) {
    // create an instance of CompletableFuture
    CompletableFuture<T> completable =
        new CompletableFuture<T>() {
          @Override
          public boolean cancel(boolean mayInterruptIfRunning) {
            // propagate cancel to the listenable future
            boolean result = listenableFuture.cancel(mayInterruptIfRunning);
            super.cancel(mayInterruptIfRunning);
            return result;
          }
        };
    // add callback
    Futures.addCallback(
        listenableFuture,
        new FutureCallback<T>() {
          @Override
          public void onSuccess(T result) {
            completable.complete(result);
          }

          @Override
          public void onFailure(Throwable t) {
            completable.completeExceptionally(t);
          }
        });
    return completable;
  }
}
