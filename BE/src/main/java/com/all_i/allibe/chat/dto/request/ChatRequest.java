package com.all_i.allibe.chat.dto.request;

public record ChatRequest(
        String content,
        Long roomId
) {
}
