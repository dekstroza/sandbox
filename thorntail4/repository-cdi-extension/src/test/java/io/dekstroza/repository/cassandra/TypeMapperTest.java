package io.dekstroza.repository.cassandra;

import com.datastax.driver.core.DataType;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.enterprise.inject.spi.AnnotatedField;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.*;

import static io.dekstroza.repository.cassandra.TypeMappings.resolveType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class TypeMapperTest {

  private static final List<Class<?>> nonCollectionTypeSet =
      ImmutableList.of(
          String.class,
          Integer.class,
          Double.class,
          Byte.class,
          Short.class,
          Float.class,
          Date.class,
          LocalDate.class,
          ByteBuffer.class,
          Long.class,
          int.class,
          short.class,
          float.class,
          long.class,
          double.class,
          byte.class,
          long.class,
          UUID.class,
          InetAddress.class);

  @Test
  public void assertStringMapsToVarChar() {
    assertThat(resolveType(createAnnotatedFieldMock(String.class))).isEqualTo(DataType.varchar());
  }

  @Test
  public void assertIntegerMapsTocInt() {
    assertThat(resolveType(createAnnotatedFieldMock(Integer.class))).isEqualTo(DataType.cint());
  }

  @Test
  public void assertIntMapsTocInt() {
    assertThat(resolveType(createAnnotatedFieldMock(int.class))).isEqualTo(DataType.cint());
  }

  @Test
  public void assertLongMapsToBigInt() {
    assertThat(resolveType(createAnnotatedFieldMock(Long.class))).isEqualTo(DataType.bigint());
  }

  @Test
  public void assertByteBufferMapsToBlog() {
    assertThat(resolveType(createAnnotatedFieldMock(ByteBuffer.class))).isEqualTo(DataType.blob());
  }

  @Test
  public void assertLocalDateMapsToDate() {
    assertThat(resolveType(createAnnotatedFieldMock(LocalDate.class))).isEqualTo(DataType.date());
  }

  @Test
  public void assertFloatMapsTocFloat() {
    assertThat(resolveType(createAnnotatedFieldMock(Float.class))).isEqualTo(DataType.cfloat());
  }

  @Test
  public void assertfloatMapsTocFloat() {
    assertThat(resolveType(createAnnotatedFieldMock(float.class))).isEqualTo(DataType.cfloat());
  }

  @Test
  public void assertShortMapsToSmallInt() {
    assertThat(resolveType(createAnnotatedFieldMock(Short.class))).isEqualTo(DataType.smallint());
  }

  @Test
  public void assertshortMapsToSmallInt() {
    assertThat(resolveType(createAnnotatedFieldMock(short.class))).isEqualTo(DataType.smallint());
  }

  @Test
  public void assertByteMapsToTinyInt() {
    assertThat(resolveType(createAnnotatedFieldMock(Byte.class))).isEqualTo(DataType.tinyint());
  }

  @Test
  public void assertbyteMapsToTinyInt() {
    assertThat(resolveType(createAnnotatedFieldMock(byte.class))).isEqualTo(DataType.tinyint());
  }

  @Test
  public void assertInetAddressMapsToInet() {
    assertThat(resolveType(createAnnotatedFieldMock(InetAddress.class))).isEqualTo(DataType.inet());
  }

  @Test
  public void assertDateMapsToDate() {
    assertThat(resolveType(createAnnotatedFieldMock(Date.class))).isEqualTo(DataType.date());
  }

  @Test
  public void assertUUIDMapsToUUID() {
    assertThat(resolveType(createAnnotatedFieldMock(UUID.class))).isEqualTo(DataType.uuid());
  }

  @Test
  public void assertSetMapsToSet() {
    nonCollectionTypeSet.forEach(
        aClass -> assertThat(resolveType(createAnnotatedFieldMock(Set.class, aClass)))
            .isEqualTo(DataType.set(resolveType(createAnnotatedFieldMock(aClass)))));
  }

  @Test
  public void assertListMapsToList() {
    nonCollectionTypeSet.forEach(
        aClass -> assertThat(resolveType(createAnnotatedFieldMock(List.class, aClass)))
            .isEqualTo(DataType.list(resolveType(createAnnotatedFieldMock(aClass)))));
  }

  @Test
  public void assertMapMapsToMap() {
    nonCollectionTypeSet.forEach(
        keyClass -> nonCollectionTypeSet.forEach(
            valueClass -> assertThat(resolveType(createAnnotatedFieldMock(Map.class, keyClass, valueClass)))
                .isEqualTo(
                    DataType.map(
                        resolveType(createAnnotatedFieldMock(keyClass)),
                        resolveType(createAnnotatedFieldMock(valueClass))))));
  }

  @Test
  public void assertUnsupportedTypeThrowsIllegalArgumentException() {
    assertThatIllegalArgumentException()
        .isThrownBy(
            () -> resolveType(createAnnotatedFieldMock(Exception.class)))
        .withMessageContaining("Unsupported type " + Exception.class.getCanonicalName());
  }

  private AnnotatedField<?> createAnnotatedFieldMock(Class<?> clazz, Class<?>... genericTypes) {
    AnnotatedField<String> mock = mock(AnnotatedField.class);
    Field field = mock(Field.class);
    ParameterizedType pt = mock(ParameterizedType.class);
    when(pt.getActualTypeArguments()).thenReturn(genericTypes);
    when(mock.getJavaMember()).thenReturn(field);
    doReturn(clazz).when(field).getType();
    doReturn(pt).when(field).getGenericType();
    return mock;
  }
}
