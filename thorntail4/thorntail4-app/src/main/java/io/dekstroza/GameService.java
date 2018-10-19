package io.dekstroza;

import io.dekstroza.repository.annotations.EnableCassandraRepository;
import io.thorntail.Thorntail;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@EnableCassandraRepository
@ApplicationPath("/")
public class GameService extends Application {
  public static void main(String... args) throws Exception {
    Thorntail.run();
  }
}
