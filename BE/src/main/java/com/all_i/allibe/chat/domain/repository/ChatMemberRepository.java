package com.all_i.allibe.chat.domain.repository;

import com.all_i.allibe.chat.domain.ChatMember;
import com.all_i.allibe.chat.dto.response.ChatMemberQueryResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long>, ChatMemberRepositoryCustom {


    List<ChatMember> findByChatRoomId(Long chatRoomId);

    List<ChatMember> findByMemberId(Long memberId);

    boolean existsByChatRoomIdAndMemberId(Long chatRoomId, Long id);
}
