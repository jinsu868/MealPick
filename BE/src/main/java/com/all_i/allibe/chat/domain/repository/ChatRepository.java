package com.all_i.allibe.chat.domain.repository;

import com.all_i.allibe.chat.domain.Chat;
import com.all_i.allibe.chat.dto.response.ChatQueryResponse;
import com.all_i.allibe.chat.dto.response.ChatRelayRequest;
import com.all_i.allibe.chat.dto.response.ChatResponse;
import com.all_i.allibe.common.dto.PageInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRepository {

    private final ChatJpaRepository chatJpaRepository;

    public Chat save(Chat chat) {
        return chatJpaRepository.save(chat);
    }

    public PageInfo<ChatResponse> findAll(
            Long chatRoomId,
            Long memberId,
            String pageToken,
            int pageSize
    ) {
        var data = chatJpaRepository.findAll(
                chatRoomId,
                memberId,
                pageToken,
                pageSize
        );
        if (data.size() <= pageSize) {
            return PageInfo.of(null, data, false);
        }

        var lastData = data.get(data.size() - 1);
        data.remove(data.size() - 1);
        String nextPageToken = String.valueOf(lastData.id());

        return PageInfo.of(nextPageToken, data, true);
    }

    public List<ChatQueryResponse> findByChatIdIn(List<Long> chatIds) {
        return chatJpaRepository.findByIdIn(chatIds);
    }

    public Boolean existsNextChat(
            Long chatRoomId,
            Long memberId,
            String pageToken
    ) {
        return chatJpaRepository.existsNextChat(
                chatRoomId,
                memberId,
                pageToken
        );
    }
}
