package com.all_i.allibe.follow.followrequest.domain.repository;

import com.all_i.allibe.follow.followrequest.domain.FollowHistory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.all_i.allibe.follow.followrequest.domain.FollowHistory.FollowRequestStatus.STAND_BY;
import static com.all_i.allibe.follow.followrequest.domain.QFollowHistory.followHistory;

@Repository
@RequiredArgsConstructor
public class FollowRequestQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<FollowHistory> findFollowRequests(
            Long recipientId,
            String pageToken,
            int limit
    ) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(followHistory.recipientId.eq(recipientId))
                .and(followHistory.followRequestStatus.eq(STAND_BY));

        if (pageToken != null) {
            builder.and(followHistory.id.lt(Long.parseLong(pageToken)));
        }

        return queryFactory
                .selectFrom(followHistory)
                .where(builder)
                .orderBy(followHistory.id.desc())
                .limit(limit)
                .fetch();
    }
}