package io.dekstroza;

import io.micronaut.core.annotation.TypeHint;
import io.micronaut.runtime.Micronaut;

@TypeHint(value={org.HdrHistogram.Histogram.class,org.HdrHistogram.ConcurrentHistogram.class})
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}