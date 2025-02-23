package com.all_i.allibe.chat.dto.response;

public record ChatRoomCreateResponse(
        Long id,
        boolean isExist
) {
    public static ChatRoomCreateResponse of(
            Long id,
            boolean isExist
    ) {
        return new ChatRoomCreateResponse(
                id,
                isExist
        );
    }
}
