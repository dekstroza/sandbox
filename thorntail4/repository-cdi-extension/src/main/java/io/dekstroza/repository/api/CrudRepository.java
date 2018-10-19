package io.dekstroza.repository.api;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Crud repository interface
 *
 * @param <X> Entity type
 * @param <I> Parition key type
 */
public interface CrudRepository<X, I> {

  /**
   * Find entity by Id
   *
   * @param id, specify id of this entity
   * @return Optional, empty if entity does not exist
   */
  Optional<X> findById(@NotNull I id);

  /**
   * Create entity in Cassandra
   *
   * @param entity Entity to persist in Cassandra
   * @return Persisted entity
   */
  X create(@NotNull X entity);

  /**
   * Update entity in Cassandra
   *
   * @param entity, Entity to update
   * @return Updated entity
   */
  X update(@NotNull X entity);

  /**
   * Delete entity from Cassandra
   *
   * @param entity, Entity to delete
   */
  void delete(@NotNull X entity);

  /**
   * Find by Id, asynchronous version
   *
   * @param id, Find entity with this Id
   * @return CompletableFuture containing Optional, which will be empty if entity does not exist.
   */
  CompletableFuture<Optional<X>> findByIdAsync(@NotNull I id);

  /**
   * Create entity in Cassandra, asynchronous version
   *
   * @param entity Entity to save in Cassandra database
   * @return CompletableFuture holding persisted entity
   */
  CompletableFuture<X> createAsync(@NotNull X entity);

  /**
   * Update entity in Cassandra, asynchronous version
   *
   * @param entity Entity to update in Cassandra database
   * @return CompletableFuture holding updated entity
   */
  CompletableFuture<X> updateAsync(@NotNull X entity);

  /**
   * Delete entity in Cassandra, asynchronous version
   *
   * @param entity Entity to delete in Cassandra
   * @return CompletableFuture of Void
   */
  CompletableFuture<Void> deleteAsync(@NotNull X entity);
}
