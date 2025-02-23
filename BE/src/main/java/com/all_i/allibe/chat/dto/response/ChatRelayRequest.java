package com.all_i.allibe.chat.dto.response;

import java.time.LocalDateTime;

public record ChatRelayRequest(
        Long id,
        Long senderId,
        Long receiverId,
        Long roomId,
        String profileImageUrl,
        LocalDateTime createdAt,
        String content
) {
    public static ChatRelayRequest of(
            Long id,
            Long senderId,
            Long receiverId,
            Long roomId,
            String profileImageUrl,
            LocalDateTime createdAt,
            String content
    ) {
        return new ChatRelayRequest(
                id,
                senderId,
                receiverId,
                roomId,
                profileImageUrl,
                createdAt,
                content
        );
    }
}
