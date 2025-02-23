package com.all_i.allibe.chat.domain.repository;

import com.all_i.allibe.chat.dto.response.ChatRoomQueryResponse;
import com.all_i.allibe.chat.dto.response.ChatRoomResponse;

import java.util.List;
import java.util.Optional;

public interface ChatRoomJpaRepositoryCustom {
    List<ChatRoomQueryResponse> findAll(Long memberId);

    Optional<ChatRoomResponse> findByPartnerId(
            Long chatRoomId,
            Long partnerId
    );
}
