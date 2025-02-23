package com.all_i.allibe.chat.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientIpHandshakeInterceptor implements HandshakeInterceptor {

    private static final String SERVER_ADDRESS = "serverAddress";
    private final Environment environment;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {

        String serverIp = request.getLocalAddress().getAddress().getHostAddress();
        String port = environment.getProperty("server.port");

        log.info("{} {}", serverIp, port);
        attributes.put(SERVER_ADDRESS, serverIp + ":" + port);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
