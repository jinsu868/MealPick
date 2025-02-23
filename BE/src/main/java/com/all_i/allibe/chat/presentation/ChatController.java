package com.all_i.allibe.chat.presentation;

import com.all_i.allibe.auth.presentation.annotation.AuthMember;
import com.all_i.allibe.chat.application.ChatService;
import com.all_i.allibe.chat.dto.request.ChatRequest;
import com.all_i.allibe.chat.dto.response.ChatResponse;
import com.all_i.allibe.common.dto.PageInfo;
import com.all_i.allibe.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private static final String KAFKA_CHATTING_TOPIC = ".topic.chats";
    private static final String CHAT_PARTITION_KEY_PREFIX = "room:";
    private final ChatService chatService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @MessageMapping("/message")
    public void sendMessage(
            @Payload ChatRequest chatRequest,
            SimpMessageHeaderAccessor accessor
    ) {
        Long memberId = Long.parseLong(accessor.getNativeHeader("principal").get(0));
        Long roomId = chatRequest.roomId();
        var chatRelayRequest = chatService.saveChat(
                chatRequest,
                memberId,
                roomId
        );
        kafkaTemplate.send(
                KAFKA_CHATTING_TOPIC,
                CHAT_PARTITION_KEY_PREFIX + roomId,
                chatRelayRequest
        );
    }

    @GetMapping("/api/v1/rooms/{chatRoomId}")
    public ResponseEntity<PageInfo<ChatResponse>> findAll(
            @AuthMember Member member,
            @PathVariable(name = "chatRoomId") Long chatRoomId,
            @RequestParam(required = false) String pageToken
    ) {
        return ResponseEntity.ok(chatService.findAll(
                chatRoomId,
                member,
                pageToken
        ));
    }
}
