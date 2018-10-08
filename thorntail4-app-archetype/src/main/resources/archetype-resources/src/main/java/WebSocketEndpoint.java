#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint("/ws")
public class WebSocketEndpoint {

    private static final Logger log = LoggerFactory.getLogger(WebSocketEndpoint.class);
    private static final List<Session> sessions = new CopyOnWriteArrayList<>();

    @OnOpen
    public void onCreateSession(final Session session) {
        sessions.add(session);
        log.info("Added session: {}", session);
    }

    @OnError
    public void onError(Throwable error) {
        log.error("Error caught:", error);
    }

    @OnMessage
    private void onMessage(String message, Session session) {
        log.info("Got message {}", message);
        sessions.stream().filter(s -> (s != null && s.isOpen())).forEach(openSession -> {
            openSession.getAsyncRemote().sendText(message);
            log.info("Sent msg {} to session {}", message, openSession);
        });
    }

    @OnClose
    public void onCloseSession(final Session session) {
        if (sessions.remove(session)) {
            log.info("Removed session {}", session);
        }
    }

    public void sendMessage(final String message) {
        sessions.stream().filter(session -> (session != null && session.isOpen())).forEach(
                   openSession -> openSession.getAsyncRemote().sendText(message));
    }

}
