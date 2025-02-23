package com.all_i.allibe.chat.domain.repository;

import com.all_i.allibe.chat.domain.ChatRoom;
import com.all_i.allibe.chat.dto.response.ChatRoomQueryResponse;
import com.all_i.allibe.chat.dto.response.ChatRoomResponse;
import com.all_i.allibe.common.dto.PageInfo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {
    private final ChatRoomJpaRepository chatRoomJpaRepository;

    public List<ChatRoomQueryResponse> findAll(Long memberId) {
        return chatRoomJpaRepository.findAll(memberId);
    }

    public ChatRoom save(ChatRoom chatRoom) {
        return chatRoomJpaRepository.save(chatRoom);
    }

    public boolean existsById(Long chatRoomId) {
        return chatRoomJpaRepository.existsById(chatRoomId);
    }

    public Optional<ChatRoomResponse> findChatRoomByPartnerId(
            Long chatRoomId,
            Long partnerId
    ) {
        return chatRoomJpaRepository.findByPartnerId(
                chatRoomId,
                partnerId
        );
    }

    public Optional<ChatRoom> findById(Long roomId) {
        return chatRoomJpaRepository.findById(roomId);
    }
}
