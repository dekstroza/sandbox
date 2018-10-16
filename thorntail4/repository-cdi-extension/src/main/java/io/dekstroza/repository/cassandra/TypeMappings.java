package io.dekstroza.repository.cassandra;

import com.datastax.driver.core.DataType;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.*;

public class TypeMappings {

    public static final Map<Class, DataType> typeMappings;

    static {
        Map<Class, DataType> mappingTypes = new HashMap<>();
        mappingTypes.put(String.class, DataType.varchar());
        mappingTypes.put(Integer.class, DataType.cint());
        mappingTypes.put(int.class, DataType.cint());
        mappingTypes.put(Long.class, DataType.bigint());
        mappingTypes.put(long.class, DataType.bigint());
        mappingTypes.put(ByteBuffer.class, DataType.blob());
        mappingTypes.put(LocalDate.class, DataType.date());
        mappingTypes.put(Double.class, DataType.cdouble());
        mappingTypes.put(double.class, DataType.cdouble());
        mappingTypes.put(Float.class, DataType.cfloat());
        mappingTypes.put(float.class, DataType.cfloat());
        mappingTypes.put(Short.class, DataType.smallint());
        mappingTypes.put(short.class, DataType.smallint());
        mappingTypes.put(Byte.class, DataType.tinyint());
        mappingTypes.put(byte.class, DataType.tinyint());

        mappingTypes.put(InetAddress.class, DataType.inet());
        mappingTypes.put(Date.class, DataType.date());
        mappingTypes.put(UUID.class, DataType.uuid());

        //mappingTypes.put(List.class, DataType.list());
        //mappingTypes.put(Map.class, DataType.map());
        //mappingTypes.put(Set.class, DataType.set());

        typeMappings = Collections.unmodifiableMap(mappingTypes);
    }
}
