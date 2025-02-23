package com.all_i.allibe.chat.domain.repository;

import com.all_i.allibe.chat.domain.QChat;
import com.all_i.allibe.chat.dto.response.ChatQueryResponse;
import com.all_i.allibe.chat.dto.response.ChatRelayRequest;
import com.all_i.allibe.chat.dto.response.ChatResponse;
import com.all_i.allibe.member.domain.QMember;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.all_i.allibe.chat.domain.QChat.chat;
import static com.all_i.allibe.member.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class ChatJpaRepositoryImpl implements ChatJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatResponse> findAll(
            Long chatRoomId,
            Long memberId,
            String pageToken,
            int chatPageSize
    ) {
        return queryFactory.select(Projections.constructor(
                        ChatResponse.class,
                        chat.id,
                        member.id,
                        chat.chatRoomId,
                        member.profileImage,
                        chat.createdAt,
                        chat.content
                )).from(chat)
                .leftJoin(member).on(member.id.eq(chat.memberId))
                .where(isInRange(pageToken), chat.chatRoomId.eq(chatRoomId))
                .orderBy(chat.id.desc())
                .limit(chatPageSize + 1)
                .fetch();
    }

    @Override
    public List<ChatQueryResponse> findByIdIn(List<Long> chatIds) {
        return queryFactory.select(
                Projections.constructor(
                        ChatQueryResponse.class,
                        chat.id,
                        chat.content,
                        chat.createdAt))
                .from(chat)
                .where(chat.id.in(chatIds))
                .fetch();
    }

    @Override
    public Boolean existsNextChat(
            Long chatRoomId,
            Long memberId,
            String pageToken
    ) {
        Integer result = queryFactory
                .selectOne()
                .from(chat)
                .leftJoin(member).on(chat.memberId.eq(member.id))
                .where(isInRange(pageToken), member.id.eq(memberId))
                .fetchFirst();

        return result != null;
    }

    private BooleanExpression isInRange(String pageToken) {
        if (pageToken == null) {
            return null;
        }

        return chat.id.loe(Long.valueOf(pageToken));
    }
}
