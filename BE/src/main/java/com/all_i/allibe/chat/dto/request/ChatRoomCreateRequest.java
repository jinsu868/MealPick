package com.all_i.allibe.chat.dto.request;

public record ChatRoomCreateRequest(
        String name,
        Long partnerId
) {
}
