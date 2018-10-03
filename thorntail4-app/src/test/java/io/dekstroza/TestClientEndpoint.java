package io.dekstroza;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.Session;

@ClientEndpoint
public class TestClientEndpoint {

    private static final Logger log = LoggerFactory.getLogger(TestClientEndpoint.class);

    @OnClose
    public void closed(Session session) {
        log.info("OnClose called.");
    }

    @OnError
    public void onError(Throwable error) {
        log.error("Error caught:", error);
    }
}
