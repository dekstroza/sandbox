package io.dekstroza;

import io.dekstroza.repository.annotations.CassandraConfig;
import io.thorntail.Thorntail;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@CassandraConfig
@ApplicationPath("/")
public class MyApplication extends Application {
  public static void main(String... args) throws Exception {
    Thorntail.run();
  }
}
