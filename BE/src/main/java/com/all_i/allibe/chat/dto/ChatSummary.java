package com.all_i.allibe.chat.dto;

import java.time.LocalDateTime;

public record ChatSummary(
        String content,
        LocalDateTime createdAt
) {
}
