package com.all_i.allibe.chat.domain.repository;

import com.all_i.allibe.chat.dto.response.ChatQueryResponse;
import com.all_i.allibe.chat.dto.response.ChatRelayRequest;

import com.all_i.allibe.chat.dto.response.ChatResponse;
import java.util.List;

public interface ChatJpaRepositoryCustom {

    List<ChatResponse> findAll(
            Long chatRoomId,
            Long memberId,
            String pageToken,
            int chatPageSize
    );

    List<ChatQueryResponse> findByIdIn(List<Long> chatIds);

    Boolean existsNextChat(
            Long chatRoomId,
            Long memberId,
            String pageToken
    );

}
