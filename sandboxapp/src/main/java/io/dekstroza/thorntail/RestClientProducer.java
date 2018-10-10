package io.dekstroza.thorntail;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;

@ApplicationScoped
public class RestClientProducer {

    @Inject
    private Logger log;

    @RestClient
    @Produces
    public SomeServiceAPI produceSomeServiceClient(InjectionPoint injectionPoint) {
        try {
            return RestClientBuilder.newBuilder().baseUrl(new URL("http://localhost:8080/")).build(SomeServiceAPI.class);
        } catch (MalformedURLException e) {
            log.error("Error creating url", e);
            throw new RuntimeException(e);
        }
    }
}
