package io.dekstroza.repository.cdi.annotations;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CassandraConfig {
    String[] contact_points() default {"127.0.0.1"};
}
