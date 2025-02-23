package com.all_i.allibe.chat.dto.response;

import java.time.LocalDateTime;

public record ChatQueryResponse(
        Long id,
        String content,
        LocalDateTime createdAt
) {
}
