package com.all_i.allibe.chat.dto;

import com.all_i.allibe.chat.dto.response.ChatResponse;
import com.all_i.allibe.chat.dto.response.ChatRoomResponse;

public record ChatCreateEvent(
        Long roomId,
        ChatResponse chatResponse,
        Long senderId,
        Long receiverId,
        Long beforeDisplayIdx,
        ChatRoomResponse myChatResponse,
        ChatRoomResponse partnerChatResponse
) {
}
