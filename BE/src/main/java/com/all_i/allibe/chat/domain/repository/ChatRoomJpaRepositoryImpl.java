package com.all_i.allibe.chat.domain.repository;

import com.all_i.allibe.chat.dto.response.ChatRoomQueryResponse;
import com.all_i.allibe.chat.dto.response.ChatRoomResponse;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.all_i.allibe.chat.domain.QChat.chat;
import static com.all_i.allibe.chat.domain.QChatMember.chatMember;
import static com.all_i.allibe.chat.domain.QChatRoom.chatRoom;
import static com.all_i.allibe.member.domain.QMember.member;

@RequiredArgsConstructor
@Repository
public class ChatRoomJpaRepositoryImpl implements ChatRoomJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatRoomQueryResponse> findAll(Long memberId) {
        return queryFactory.select(Projections.constructor(
                ChatRoomQueryResponse.class,
                chatRoom.id,
                chatRoom.name,
                chatMember.displayIdx
                ))
                .from(chatRoom)
                .leftJoin(chatMember).on(chatMember.chatRoomId.eq(chatRoom.id))
                .leftJoin(member).on(chatMember.memberId.eq(member.id))
                .where(chatMember.memberId.eq(memberId))
                .orderBy(chatMember.displayIdx.desc())
                .fetch();
    }

    @Override
    public Optional<ChatRoomResponse> findByPartnerId(
            Long chatRoomId,
            Long partnerId
    ) {
        return Optional.ofNullable(queryFactory.select(Projections.constructor(
                        ChatRoomResponse.class,
                        chatRoom.id,
                        chatRoom.name,
                        chatMember.displayIdx,
                        member.name,
                        Expressions.constant(""),
                        chatRoom.createdAt,
                        member.profileImage))
                .from(chatRoom)
                .leftJoin(chatMember).on(chatMember.chatRoomId.eq(chatRoom.id))
                .leftJoin(member).on(chatMember.memberId.eq(member.id))
                .where(member.id.eq(partnerId).and(chatRoom.id.eq(chatRoomId)))
                .fetchOne());
    }
}
