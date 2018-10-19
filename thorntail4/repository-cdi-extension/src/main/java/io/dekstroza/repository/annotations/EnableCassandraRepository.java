package io.dekstroza.repository.annotations;

import java.lang.annotation.*;

/** Enable cassandra repository */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EnableCassandraRepository {
  boolean create() default true;
}
