package com.all_i.allibe.chat.application;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Deprecated : 채팅 캐시 제거에 따라서 사용하지 않게 됐습니다.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatCacheInvalidateScheduler {
    private static final int KEEP_SIZE = 100;
    private static final int CYCLE = 7;
    private static final String CHAT_KEYS = "rooms:*";

    private final RedisTemplate<String, Object> redisTemplate;

//    @Scheduled(cron = "0 0 3 * * *")
    public void invalidateChatCache() {
        log.info("Chat cache invalidated");
        Set<String> chatRoomKeys = redisTemplate.keys(CHAT_KEYS);

        if (chatRoomKeys.isEmpty()) {
            return;
        }

        int todayGroup = LocalDateTime.now().getDayOfWeek().getValue() % CYCLE;
        for (String chatRoomKey : chatRoomKeys) {
            int key = Math.abs(chatRoomKey.hashCode() % CYCLE);
            if (key != todayGroup) continue;

            Long totalSize = redisTemplate.opsForZSet().size(chatRoomKey);
            if (totalSize != null && totalSize > KEEP_SIZE) {
                long removeSize = totalSize - KEEP_SIZE;
                redisTemplate.opsForZSet().removeRange(chatRoomKey, 0, removeSize - 1);
            }
        }
    }
}
