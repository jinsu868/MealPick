package com.all_i.allibe.chat.dto;

import com.all_i.allibe.chat.dto.response.ChatRoomResponse;

public record ChatRoomCreateEvent(
        Long senderId,
        Long receiverId,
        ChatRoomResponse myChatRoomResponse,
        ChatRoomResponse partnerChatRoomResponse
) {
}