package io.dekstroza.thorntail;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;

import javax.inject.Inject;

@Health
public class MyHealthCheck implements HealthCheck {

    @Inject
    private Logger logger;

    @RestClient
    @Inject
    private SomeServiceAPI someServiceAPI;

    @Override
    public HealthCheckResponse call() {
        logger.info("Healthcheck has been called");
        String resp = someServiceAPI.getHelloWorld();
        logger.info("Response was: {}", resp);
        return HealthCheckResponse.named("Dekstroza's Healthcheck").up().build();
    }
}
