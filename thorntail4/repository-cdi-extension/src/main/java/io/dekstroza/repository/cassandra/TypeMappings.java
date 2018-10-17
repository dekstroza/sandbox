package io.dekstroza.repository.cassandra;

import com.datastax.driver.core.DataType;

import javax.enterprise.inject.spi.AnnotatedField;
import java.lang.reflect.ParameterizedType;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.*;

class TypeMappings {

  static final Map<String, DataType> typeMappings;

  static {
    Map<String, DataType> mappingTypes = new HashMap<>();
    mappingTypes.put(String.class.getCanonicalName(), DataType.varchar());
    mappingTypes.put(Integer.class.getCanonicalName(), DataType.cint());
    mappingTypes.put(int.class.getCanonicalName(), DataType.cint());
    mappingTypes.put(Long.class.getCanonicalName(), DataType.bigint());
    mappingTypes.put(long.class.getCanonicalName(), DataType.bigint());
    mappingTypes.put(ByteBuffer.class.getCanonicalName(), DataType.blob());
    mappingTypes.put(LocalDate.class.getCanonicalName(), DataType.date());
    mappingTypes.put(Double.class.getCanonicalName(), DataType.cdouble());
    mappingTypes.put(double.class.getCanonicalName(), DataType.cdouble());
    mappingTypes.put(Float.class.getCanonicalName(), DataType.cfloat());
    mappingTypes.put(float.class.getCanonicalName(), DataType.cfloat());
    mappingTypes.put(Short.class.getCanonicalName(), DataType.smallint());
    mappingTypes.put(short.class.getCanonicalName(), DataType.smallint());
    mappingTypes.put(Byte.class.getCanonicalName(), DataType.tinyint());
    mappingTypes.put(byte.class.getCanonicalName(), DataType.tinyint());
    mappingTypes.put(InetAddress.class.getCanonicalName(), DataType.inet());
    mappingTypes.put(Date.class.getCanonicalName(), DataType.date());
    mappingTypes.put(UUID.class.getCanonicalName(), DataType.uuid());

    typeMappings = Collections.unmodifiableMap(mappingTypes);
  }

  static DataType resolveType(AnnotatedField<?> t) {
    if (t.getJavaMember().getType().isAssignableFrom(List.class)) {
      String typeName =
          ((ParameterizedType) t.getJavaMember().getGenericType())
              .getActualTypeArguments()[0].getTypeName();
      return DataType.list(typeMappings.get(typeName));

    } else if (t.getJavaMember().getType().isAssignableFrom(Map.class)) {
      String keyTypeName =
          ((ParameterizedType) t.getJavaMember().getGenericType())
              .getActualTypeArguments()[0].getTypeName();
      String valueTypeName =
          ((ParameterizedType) t.getJavaMember().getGenericType())
              .getActualTypeArguments()[1].getTypeName();
      return DataType.map(typeMappings.get(keyTypeName), typeMappings.get(valueTypeName));

    } else if (t.getJavaMember().getType().isAssignableFrom(Set.class)) {
      String typeName =
          ((ParameterizedType) t.getJavaMember().getGenericType())
              .getActualTypeArguments()[0].getTypeName();
      return DataType.set(typeMappings.get(typeName));

    } else {
      return Optional.ofNullable(typeMappings.get(t.getJavaMember().getType().getCanonicalName()))
          .orElseThrow(
              () ->
                  new IllegalStateException(
                      "Unsupported type " + t.getJavaMember().getType().getCanonicalName()));
    }
  }
}
