package com.all_i.allibe.chat.application;

import com.all_i.allibe.chat.dto.ChatCreateEvent;
import com.all_i.allibe.chat.dto.ChatRoomCreateEvent;
import com.all_i.allibe.chat.dto.response.ChatResponse;
import com.all_i.allibe.chat.dto.response.ChatRoomResponse;
import com.all_i.allibe.common.util.JsonUtil;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatEventHandler {
    private static final String MEMBERS_CHAT_ROOMS_KEY = "members:%s:rooms";
    private static final String CHAT_ROOM_KEY = "rooms:%s";
    private static final int KEY_EXPIRE_TIME = 604800;

    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonUtil jsonUtil;

    @TransactionalEventListener
    public void handleCreateChatRoom(ChatRoomCreateEvent event) {
        String senderKey = String.format(MEMBERS_CHAT_ROOMS_KEY, event.senderId());
        String receiverKey = String.format(MEMBERS_CHAT_ROOMS_KEY, event.receiverId());

        saveChatRoomCache(event.myChatRoomResponse(), senderKey);
        saveChatRoomCache(event.partnerChatRoomResponse(), receiverKey);
    }

    @TransactionalEventListener
    public void handleCreateChat(ChatCreateEvent event) {
        String chatRoomKey = String.format(CHAT_ROOM_KEY, event.roomId());
        saveChatCache(event.chatResponse(), chatRoomKey);

        String myKey = String.format(MEMBERS_CHAT_ROOMS_KEY, event.senderId());
        String partnerKey = String.format(MEMBERS_CHAT_ROOMS_KEY, event.receiverId());

        updateChatRoomCache(event.beforeDisplayIdx(), event.myChatResponse(), myKey);
        updateChatRoomCache(event.beforeDisplayIdx(), event.partnerChatResponse(), partnerKey);
    }

    private void saveChatCache(
            ChatResponse chatResponse,
            String chatRoomKey
    ) {
        var zSetOps = redisTemplate.opsForZSet();
        if (redisTemplate.hasKey(chatRoomKey)) {
            zSetOps.add(
                    chatRoomKey,
                    jsonUtil.convertToJson(chatResponse),
                    chatResponse.id()
            );
            redisTemplate.expire(chatRoomKey, KEY_EXPIRE_TIME, TimeUnit.SECONDS);
        }
    }

    private void updateChatRoomCache(
            Long displayIdx,
            ChatRoomResponse chatRoomResponse,
            String key
    ) {
        var zSetOps = redisTemplate.opsForZSet();
        if (redisTemplate.hasKey(key)) {
            zSetOps.removeRangeByScore(key, displayIdx, displayIdx);
            zSetOps.add(
                    key,
                    jsonUtil.convertToJson(chatRoomResponse),
                    chatRoomResponse.displayIdx()
            );
            redisTemplate.expire(key, KEY_EXPIRE_TIME, TimeUnit.SECONDS);
        }
    }

    private void saveChatRoomCache(
            ChatRoomResponse chatRoomResponse,
            String key
    ) {
        var zSetOps = redisTemplate.opsForZSet();
        if (redisTemplate.hasKey(key)) {
            zSetOps.add(
                    key,
                    jsonUtil.convertToJson(chatRoomResponse),
                    chatRoomResponse.displayIdx()
            );
            redisTemplate.expire(key, KEY_EXPIRE_TIME, TimeUnit.SECONDS);
        }
    }
}
