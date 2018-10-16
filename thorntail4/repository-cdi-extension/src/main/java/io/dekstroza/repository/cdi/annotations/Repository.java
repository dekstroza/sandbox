package io.dekstroza.repository.cdi.annotations;

import java.lang.annotation.*;

@Inherited
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Repository {
}
