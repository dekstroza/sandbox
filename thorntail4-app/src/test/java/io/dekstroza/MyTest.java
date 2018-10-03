package io.dekstroza;

import io.thorntail.test.ThorntailTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ContainerProvider;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;

@RunWith(ThorntailTestRunner.class)
public class MyTest {

    private static final Logger log = LoggerFactory.getLogger(MyTest.class);
    private static final String SENT_MESSAGE = "Hello Testing World";
    private static final String SERVER_URL = "ws://localhost:8080/ws";
    private CountDownLatch latch = new CountDownLatch(1);

    @Test
    public void test() {
        when().get("/").then().statusCode(200).body(containsString("Hello World"));
    }

    @Test
    public void testWebSocket() throws Exception {
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        Session session = webSocketContainer.connectToServer(TestClientEndpoint.class, new URI(SERVER_URL));
        session.addMessageHandler(new MessageHandler.Whole<String>() {

            @Override
            public void onMessage(String msg) {
                log.info("Got message: {}", msg);
                Assert.assertEquals(SENT_MESSAGE, msg);
                latch.countDown();
            }
        });
        session.getAsyncRemote().sendText(SENT_MESSAGE);
        latch.await();
        if(session!= null && session.isOpen()) {
            session.close();
        }
    }

}

