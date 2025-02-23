package com.all_i.allibe.chat.event;

import com.all_i.allibe.chat.dto.response.ChatRelayRequest;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private static final String MEMBER_KEY_PREFIX = "member:";
    private static final String CHATTING_CHANNEL_PREFIX = "/topic/rooms/";
    private static final String KAFKA_CHATTING_TOPIC = ".topic.chats";
    private static final String PROTOCOL = "http://";

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    private final RedisTemplate<String, String> redisTemplate;
    private final RestTemplate restTemplate;

    @KafkaListener(
            topics = KAFKA_CHATTING_TOPIC,
            properties = "max.poll.records:20",
            groupId = "con-group-1"
    )
    public void consume(List<ChatRelayRequest> chatRelayRequests) {
        Map<Long, String> memberIdToServerAddress = new HashMap<>();
        Map<String, List<ChatRelayRequest>> serverAddressToChatResponse = new HashMap<>();
        for (ChatRelayRequest chatRelayRequest : chatRelayRequests) {
            memberIdToServerAddress.put(
                    chatRelayRequest.senderId(),
                    getDestinationUrl(
                            chatRelayRequest.senderId(),
                            chatRelayRequest.roomId()
                    )
            );
            memberIdToServerAddress.put(
                    chatRelayRequest.receiverId(),
                    getDestinationUrl(
                            chatRelayRequest.receiverId(),
                            chatRelayRequest.roomId()
                    )
            );
        }

        chatRelayRequests.forEach(chatRelayRequest -> {
            String senderServerAddressKey = memberIdToServerAddress.get(chatRelayRequest.senderId());
            String receiverServerAddressKey = memberIdToServerAddress.get(chatRelayRequest.receiverId());
            if (senderServerAddressKey.equals(receiverServerAddressKey)) {
                serverAddressToChatResponse
                        .computeIfAbsent(senderServerAddressKey, key -> new ArrayList<>())
                        .add(chatRelayRequest);
            } else {
                serverAddressToChatResponse
                        .computeIfAbsent(senderServerAddressKey, key -> new ArrayList<>())
                        .add(chatRelayRequest);
                serverAddressToChatResponse
                        .computeIfAbsent(receiverServerAddressKey, key -> new ArrayList<>())
                        .add(chatRelayRequest);
            }
        });

        relayChatMessage(serverAddressToChatResponse);
    }

    @Async("chatRelayExecutor")
    public void relayChatMessage(Map<String, List<ChatRelayRequest>> serverAddressToChatResponse) {
        serverAddressToChatResponse.forEach((address, chat) -> {
            log.info("consumer / address, chat : {} {}", address, chat);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<List<ChatRelayRequest>> requestEntity = new HttpEntity<>(chat, headers);

                    ResponseEntity<String> response = restTemplate.exchange(
                            URI.create(address),
                            HttpMethod.POST,
                            requestEntity,
                            String.class
                    );

                    log.info("Received response: {}", response.getStatusCode());
                }
        );
    }

    private String getDestinationUrl(Long memberId, Long roomId) {

        String serverAddress = redisTemplate.opsForValue().get(MEMBER_KEY_PREFIX + memberId);
        if (serverAddress != null) {
            serverAddress = serverAddress.replace("\"", "");
        }

        return PROTOCOL + serverAddress + CHATTING_CHANNEL_PREFIX + roomId;
    }
}
