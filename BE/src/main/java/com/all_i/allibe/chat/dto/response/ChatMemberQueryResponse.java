package com.all_i.allibe.chat.dto.response;

public record ChatMemberQueryResponse(
        Long chatRoomId,
        Long memberId,
        String name,
        String profileImageUrl
) {
}
