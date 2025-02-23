package com.all_i.allibe.chat.application;

import com.all_i.allibe.chat.dto.response.ChatRelayRequest;
import com.all_i.allibe.chat.dto.response.ChatResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRelayService {

    private static final String CHAT_CHANNEL_PREFIX = "/topic/rooms/";
    private static final String CHAT_ROOM_CHANNEL = "/topic/rooms/%s/member";

    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate brokerMessagingTemplate;


    public void send(
            List<ChatRelayRequest> chatRelayRequests,
            Long roomId
    ) {
        Set<Long> memberIds = new HashSet<>();
        chatRelayRequests.forEach(chatRelayRequest -> {
            brokerMessagingTemplate.convertAndSend(
                    CHAT_CHANNEL_PREFIX + roomId,
                    ChatResponse.of(
                            chatRelayRequest.id(),
                            chatRelayRequest.senderId(),
                            chatRelayRequest.roomId(),
                            chatRelayRequest.profileImageUrl(),
                            chatRelayRequest.createdAt(),
                            chatRelayRequest.content()
                    )
            );
            memberIds.add(chatRelayRequest.receiverId());
            memberIds.add(chatRelayRequest.senderId());
        });

        memberIds.forEach(receiverId -> brokerMessagingTemplate.convertAndSend(
                String.format(CHAT_ROOM_CHANNEL, receiverId),
                chatRoomService.findAll(receiverId)
        ));
    }
}
