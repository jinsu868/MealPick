package com.all_i.allibe.follow.followrequest.domain.repository;

import com.all_i.allibe.follow.followrequest.domain.FollowHistory;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowRequestRepositoryCustom {

    Optional<FollowHistory> findLastAcceptedRequest(
            @Param("requesterId") Long memberId,
            @Param("recipientId") Long recipientId
    );
}
