package de.thb.tictactoe_server.handler;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles websocket interaction with clients for gameSessions
 * WIP
 */

public class ClientSocketHandler implements WebSocketHandler {

    List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>());

    @Override
    public List<String> getSubProtocols() {
        return WebSocketHandler.super.getSubProtocols();
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // register session in session list
        webSocketSessions.add(session);

        //needs a state machine in gamesessionservice
        return session
                .send( session.receive()
                        .map(msg -> "RECEIVED ON SERVER :: " + msg.getPayloadAsText())
                        .map(session::textMessage)
                );
    }
}
