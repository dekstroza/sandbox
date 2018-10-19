package io.dekstroza.repository.cassandra;

import com.datastax.driver.core.DataType;

import javax.enterprise.inject.spi.AnnotatedField;
import java.lang.reflect.ParameterizedType;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.*;

import static java.util.Optional.ofNullable;

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
            return DataType.list(typeMappings.get(getTypeName(t, 0)));

        } else if (t.getJavaMember().getType().isAssignableFrom(Map.class)) {
            return DataType.map(typeMappings.get(getTypeName(t, 0)), typeMappings.get(getTypeName(t, 1)));

        } else if (t.getJavaMember().getType().isAssignableFrom(Set.class)) {
            return DataType.set(typeMappings.get(getTypeName(t, 0)));

        } else {
            return ofNullable(typeMappings.get(t.getJavaMember().getType().getCanonicalName())).orElseThrow(
                       () -> new IllegalArgumentException("Unsupported type " + t.getJavaMember().getType().getCanonicalName()));
        }
    }

    static String getTypeName(AnnotatedField<?> annotatedField, int argIndex) {
        return ((ParameterizedType) annotatedField.getJavaMember().getGenericType()).getActualTypeArguments()[argIndex].getTypeName();
    }
}
