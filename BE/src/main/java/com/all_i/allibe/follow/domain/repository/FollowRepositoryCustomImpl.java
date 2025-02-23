package com.all_i.allibe.follow.domain.repository;

import com.all_i.allibe.follow.domain.Follow;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.all_i.allibe.follow.domain.QFollow.follow;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryCustomImpl implements FollowRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public List<Follow> findFollowers(
            Long memberId,
            String pageToken,
            int limit
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(follow.followingId.eq(memberId));

        if (pageToken != null) {
            builder.and(follow.id.lt(Long.parseLong(pageToken)));
        }

        return queryFactory
                .selectFrom(follow)
                .where(builder)
                .orderBy(follow.id.desc())
                .limit(limit)
                .fetch();
    }

    public List<Follow> findFollows(
            Long memberId,
            String pageToken,
            int limit
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(follow.followerId.eq(memberId));

        if (pageToken != null) {
            builder.and(follow.id.lt(Long.parseLong(pageToken)));
        }

        return queryFactory
                .selectFrom(follow)
                .where(builder)
                .orderBy(follow.id.desc())
                .limit(limit)
                .fetch();
    }
}
