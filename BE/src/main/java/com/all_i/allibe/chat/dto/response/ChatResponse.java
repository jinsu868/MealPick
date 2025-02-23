package com.all_i.allibe.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public record ChatResponse(
        Long id,
        Long senderId,
        Long roomId,
        String profileImageUrl,
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime createdAt,
        String content
) {
    public static ChatResponse of(
            Long id,
            Long senderId,
            Long roomId,
            String profileImageUrl,
            LocalDateTime createdAt,
            String content
    ) {
        return new ChatResponse(
                id,
                senderId,
                roomId,
                profileImageUrl,
                createdAt,
                content
        );
    }
}
