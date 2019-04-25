package io.dekstroza;

import io.micronaut.core.annotation.TypeHint;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(info = @Info(title = "Micronaut Service", version = "1.0.0", description = "Micronaut Service API", license = @License(name = "Apache 2.0", url = "https://blog.dekstroza.io"), contact = @Contact(url = "https://blog.dekstroza.io", name = "Dejan Kitic", email = "kdejan@gmail.com")))
@TypeHint(value = { org.HdrHistogram.Histogram.class, org.HdrHistogram.ConcurrentHistogram.class,io.jaegertracing.internal.samplers.http.SamplingStrategyResponse.class })
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}