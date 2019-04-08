package io.dekstroza;

import io.micronaut.configuration.metrics.annotation.RequiresMetrics;
import io.micronaut.runtime.Micronaut;

import javax.inject.Singleton;

@Singleton
@RequiresMetrics
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}