package io.dekstroza.repository.api;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CrudRepository<X, I> {

  Optional<X> findById(@NotNull I id);

  X create(@NotNull X entity);

  X update(@NotNull X entity);

  void delete(@NotNull X entity);

  CompletableFuture<Optional<X>> findByIdAsync(@NotNull I id);

  CompletableFuture<X> createAsync(@NotNull X entity);

  CompletableFuture<X> updateAsync(@NotNull X entity);

  CompletableFuture<Void> deleteAsync(@NotNull X entity);
}
