package de.thb.tictactoe_server.config;

import de.thb.tictactoe_server.handler.ClientSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Configures server to accept websocket requests under /playerSocket
 * Replaces controller for mapping in HandlerMapping method
 * Actual websocket message handling in ClientSocketHandler
 */

@Configuration
public class ClientSocketConfig {


    @Bean
    public ClientSocketHandler clientSocketHandler() {
        return new ClientSocketHandler();
    }

    /**
     * method to map requests for websockets
     * @return maps /playerSocket to clientSocketHandler
     */
    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/playerSocket", clientSocketHandler());

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter(webSocketService());
    }

    @Bean
    public WebSocketService webSocketService() {
        return new HandshakeWebSocketService(new ReactorNettyRequestUpgradeStrategy());
    }
}
