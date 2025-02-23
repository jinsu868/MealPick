package com.all_i.allibe.chat.domain.repository;

import com.all_i.allibe.chat.domain.QChatMember;
import com.all_i.allibe.chat.dto.response.ChatMemberQueryResponse;
import com.all_i.allibe.member.domain.QMember;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.all_i.allibe.chat.domain.QChatMember.chatMember;
import static com.all_i.allibe.chat.domain.QChatRoom.chatRoom;
import static com.all_i.allibe.member.domain.QMember.*;

@Repository
@RequiredArgsConstructor
public class ChatMemberRepositoryImpl implements ChatMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatMemberQueryResponse> findAllByChatRoomIdIn(List<Long> chatRoomIds) {
        return queryFactory.select(
                Projections.constructor(
                        ChatMemberQueryResponse.class,
                        chatRoom.id,
                        member.id,
                        member.name,
                        member.profileImage))
                .from(chatRoom)
                .leftJoin(chatMember).on(chatMember.chatRoomId.eq(chatRoom.id))
                .leftJoin(member).on(chatMember.memberId.eq(member.id))
                .where(chatMember.chatRoomId.in(chatRoomIds))
                .fetch();
    }
}
