package io.dekstroza;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Collection;

import static io.micronaut.http.HttpRequest.GET;
import static io.micronaut.http.HttpRequest.POST;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class AlarmsControllerTest {

    @Inject
    @Client("/mongonaut")
    private HttpClient rxHttpClient;

    @Test
    public void testGetAllAlarms() {
        HttpRequest<Collection<Alarm>> getAllAlarmsRequest = GET("/alarms");
        final HttpResponse<Collection> response = rxHttpClient.toBlocking().exchange(getAllAlarmsRequest, Collection.class);
        assertEquals(200, response.code());

    }

    @Test
    public void testPostAlarm() {
        HttpRequest<Alarm> postRequest = POST("/alarms", new Alarm(2, "Second Test Alarm", "CRITICAL"));
        final HttpResponse<Alarm> response = rxHttpClient.toBlocking().exchange(postRequest);
        assertEquals(201, response.code());

    }
}
