package io.dekstroza.repository.cassandra;

import io.dekstroza.repository.CassandraThorntailRunner;
import io.dekstroza.repository.annotations.EnableCassandraRepository;
import io.dekstroza.repository.cassandra.model.TestCassandraModel;
import io.dekstroza.repository.cassandra.service.ServiceBean;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertNotNull;

@RunWith(CassandraThorntailRunner.class)
@EnableCassandraRepository
public class RepositoryInjectionTest {

  @Inject private ServiceBean serviceBean;

  @BeforeClass
  public static void init() {
    try {
      // Throws exceptions when starting up, which doesn't prevent it from correctly starting and
      // running, so swallow this exception
      EmbeddedCassandraServerHelper.startEmbeddedCassandra();
    } catch (Exception e) {

    }
  }

  @Test
  public void testRepositoryInjectionPoint() {
    assertNotNull(serviceBean.getRepository());
  }

  @Test
  public void testFindById_ReturnsEmptyOptional_When_NoEnityWithGivenId() {
    assertThat(serviceBean.getRepository().findById(randomUUID()).isPresent()).isFalse();
  }

  @Test
  public void testFindByIdAsync_ReturnsEmptyOptional_When_NoEnityWithGivenId() {
    assertThat(serviceBean.getRepository().findByIdAsync(randomUUID()).join().isPresent())
        .isFalse();
  }

  @Test
  public void testCreate() {
    final TestCassandraModel model = createTestModel();
    assertThat(serviceBean.getRepository().create(model)).isEqualTo(model);
  }

  @Test
  public void testCreateAsync() {
    final TestCassandraModel model = createTestModel();
    assertThat(serviceBean.getRepository().createAsync(model).join()).isEqualTo(model);
  }

  @Test
  public void testReadAsync_ReturnsEntity_When_Entity_Exists() {
    final TestCassandraModel model = createTestModel();
    serviceBean.getRepository().createAsync(model).join();
    final Optional<TestCassandraModel> optionalTestCassandraModel =
        serviceBean.getRepository().findByIdAsync(model.getUuidField()).join();

    assertThat(optionalTestCassandraModel.isPresent()).isTrue();
    assertThat(optionalTestCassandraModel.get()).isEqualTo(model);
  }

  @Test
  public void testRead_ReturnsEntity_When_Entity_Exists() {
    final TestCassandraModel model = createTestModel();
    final TestCassandraModel persistedModel = serviceBean.getRepository().create(model);
    final Optional<TestCassandraModel> optionalResult =
        serviceBean.getRepository().findById(persistedModel.getUuidField());

    assertThat(optionalResult.isPresent()).isTrue();
    assertThat(optionalResult.get()).isEqualTo(model);
  }

  @Test
  public void testUpdate_ReturnsEntity_When_Entity_Exists() {
    final TestCassandraModel model = createTestModel();

    final TestCassandraModel expectedAfterUpdate = new TestCassandraModel();
    expectedAfterUpdate.setUuidField(model.getUuidField());
    expectedAfterUpdate.setIntField(0);
    expectedAfterUpdate.setStringField("");
    // Create Entity
    serviceBean.getRepository().create(model);
    // Change some fields
    model.setIntField(0);
    model.setStringField("");
    // Assert it's updated
    assertThat(serviceBean.getRepository().update(model)).isEqualTo(expectedAfterUpdate);
  }

  @Test
  public void testUpdateAsync_ReturnsEntity_When_Entity_Exists() {
    final TestCassandraModel model = createTestModel();

    TestCassandraModel persistedModel = serviceBean.getRepository().createAsync(model).join();
    persistedModel.setIntField(0);
    persistedModel.setStringField("");
    // Update
    TestCassandraModel updatedModel =
        serviceBean.getRepository().updateAsync(persistedModel).join();

    assertThat(updatedModel.getIntField()).isEqualTo(0);
    assertThat(updatedModel.getStringField()).isEmpty();
    assertThat(updatedModel.getUuidField()).isEqualTo(persistedModel.getUuidField());
  }

  @Test
  public void testDeleteAsync_ReturnsNoExceptions_When_EntityExists() {
    final TestCassandraModel model = createTestModel();
    serviceBean.getRepository().createAsync(model).join();

    assertThatCode(() -> serviceBean.getRepository().deleteAsync(model).join())
        .doesNotThrowAnyException();
  }

  @Test
  public void testDelete_ReturnsNoExceptions_When_EntityExists() {
    final TestCassandraModel model = createTestModel();
    serviceBean.getRepository().create(model);
    assertThatCode(() -> serviceBean.getRepository().delete(model)).doesNotThrowAnyException();
    assertThat(serviceBean.getRepository().findById(model.getUuidField()).isPresent()).isFalse();
  }

  @Test
  public void testDelete_ReturnsNOException_When_EntityNotExists() {
    final TestCassandraModel model = createTestModel();
    // TODO: Should this throw Exception or return some indicator of success ?
    assertThatCode(() -> serviceBean.getRepository().delete(model)).doesNotThrowAnyException();
  }

  @Test
  public void testDeleteAsync_ReturnsNOException_When_EntityNotExists() {
    final TestCassandraModel model = createTestModel();
    // TODO: Should this throw Exception or return some indicator of success ?
    assertThatCode(() -> serviceBean.getRepository().deleteAsync(model).join())
        .doesNotThrowAnyException();
  }

  private TestCassandraModel createTestModel() {
    TestCassandraModel model = new TestCassandraModel();
    model.setUuidField(UUID.randomUUID());
    model.setIntField(123);
    model.setStringField("Hello World");
    return model;
  }

  @AfterClass
  public static void shutdown() {
    // Throws exceptions when shutting down, which doesn't prevent it from correctly starting and
    // running, so swallow this exception
    try {
      EmbeddedCassandraServerHelper.cleanDataEmbeddedCassandra("testspace");
    } catch (Exception e) {

    }
  }
}
