package com.all_i.allibe.member.domain.repository;

import com.all_i.allibe.member.dto.response.MemberSearchResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.all_i.allibe.follow.domain.QFollow.follow;
import static com.all_i.allibe.member.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<MemberSearchResponse> searchMembers(
            Long loginId,
            String keyword
    ) {
        return queryFactory
                .select(Projections.constructor(
                        MemberSearchResponse.class,
                        member.id,
                        member.name,
                        member.nickname,
                        member.profileImage,
                        follow.id.isNotNull()
                ))
                .from(member)
                .leftJoin(follow)
                .on(member.id.eq(follow.followerId)
                        .and(follow.followingId.eq(loginId)))
                .where(member.nickname.containsIgnoreCase(keyword)
                        .or(member.name.containsIgnoreCase(keyword)))
                .orderBy(follow.id.desc())
                .fetch();
    }
}
