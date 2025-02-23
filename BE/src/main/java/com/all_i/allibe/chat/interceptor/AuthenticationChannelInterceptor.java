package com.all_i.allibe.chat.interceptor;

import com.all_i.allibe.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationChannelInterceptor implements ChannelInterceptor {
    private static final String SERVER_ADDRESS = "serverAddress";
    private static final String MEMBER_KEY_PREFIX = "member:";
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {
        var headerAccessor = MessageHeaderAccessor.getAccessor(
                message,
                StompHeaderAccessor.class
        );

        if (headerAccessor.getCommand() == null) {
            return message;
        }

        if (headerAccessor.getCommand() == StompCommand.CONNECT
                || headerAccessor.getCommand() == StompCommand.SEND
        ) {
            log.info("CONNECT memberId: {}", headerAccessor.getCommand().toString());
            Long memberId = extractMemberIdFromHeader(headerAccessor);
            headerAccessor.addNativeHeader("principal", String.valueOf(memberId));
        }

        if (headerAccessor.getCommand() == StompCommand.SUBSCRIBE) {
            log.info("SUBSCRIBE START");
            Long memberId = extractMemberIdFromHeader(headerAccessor);
            log.info("SUBSCRIBE memberId: {}", memberId);
            String serverAddress = (String) headerAccessor.getSessionAttributes().get(SERVER_ADDRESS);

            redisTemplate.opsForValue().set(MEMBER_KEY_PREFIX + memberId, serverAddress);
        }

        if (headerAccessor.getCommand() == StompCommand.DISCONNECT) {
            log.info("DISCONNECT");
//            Long memberId = extractMemberIdFromHeader(headerAccessor);
//            redisTemplate.delete(MEMBER_KEY_PREFIX + memberId);
        }

        return message;
    }

    private Long extractMemberIdFromHeader(StompHeaderAccessor headerAccessor) {
        String token = String.valueOf(headerAccessor.getNativeHeader("Authorization").get(0));
        return Long.valueOf(jwtUtil.getSubject(token.split(" ")[1]));
    }
}

