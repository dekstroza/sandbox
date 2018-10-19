package io.dekstroza.repository.cassandra;

import io.thorntail.Thorntail;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@Ignore
@RunWith(JUnit4.class)
public class TypeMappingTest {

  @BeforeClass
  public static void startCassandra() throws Exception {
    EmbeddedCassandraServerHelper.startEmbeddedCassandra();
    Thorntail.run();
  }

  @Test
  public void testSomething() {
    Assert.fail();
  }

  @AfterClass
  public static void cleanup() throws Exception {
    Thorntail.current().stop();
    EmbeddedCassandraServerHelper.stopEmbeddedCassandra();
  }
}
