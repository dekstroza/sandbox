package io.dekstroza.repository.api;

import java.util.Optional;

public interface CrudRepository<X, I> {

  Optional<X> findById(I id);

  X create(X entity);

  X update(X entity);

  void delete(X entity);
}
