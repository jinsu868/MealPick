package com.all_i.allibe.chat.domain.repository;

import com.all_i.allibe.chat.dto.response.ChatMemberQueryResponse;

import java.util.List;

public interface ChatMemberRepositoryCustom {
    List<ChatMemberQueryResponse> findAllByChatRoomIdIn(List<Long> chatRoomIds);
}
