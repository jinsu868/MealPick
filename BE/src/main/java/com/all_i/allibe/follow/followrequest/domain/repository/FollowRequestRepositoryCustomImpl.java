package com.all_i.allibe.follow.followrequest.domain.repository;

import com.all_i.allibe.follow.followrequest.domain.FollowHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.all_i.allibe.follow.followrequest.domain.QFollowHistory.followHistory;

@Repository
@RequiredArgsConstructor
public class FollowRequestRepositoryCustomImpl implements FollowRequestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<FollowHistory> findLastAcceptedRequest(
            Long memberId,
            Long recipientId
    ) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(followHistory)
                        .where(
                                followHistory.requesterId.eq(memberId),
                                followHistory.recipientId.eq(recipientId),
                                followHistory.followRequestStatus.eq(FollowHistory.FollowRequestStatus.ACCEPT)
                        )
                        .orderBy(followHistory.createdAt.desc())
                        .limit(1)
                        .fetchOne()
        );
    }
}
