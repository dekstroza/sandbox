package io.dekstroza.repository.cassandra.service;

import io.dekstroza.repository.annotations.EnableCassandraRepository;
import io.dekstroza.repository.annotations.Repository;
import io.dekstroza.repository.api.CrudRepository;
import io.dekstroza.repository.cassandra.model.TestCassandraModel;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.UUID;

@EnableCassandraRepository
@ApplicationScoped
@Transactional
public class ServiceBean {
  @Repository private CrudRepository<TestCassandraModel, UUID> repository;

  public CrudRepository<TestCassandraModel, UUID> getRepository() {
    return repository;
  }
}
