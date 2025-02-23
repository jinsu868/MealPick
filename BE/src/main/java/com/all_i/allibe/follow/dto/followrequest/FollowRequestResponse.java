package com.all_i.allibe.follow.dto.followrequest;


import com.all_i.allibe.follow.followrequest.domain.FollowHistory;
import com.all_i.allibe.follow.followrequest.domain.FollowHistory.FollowRequestStatus;

public record FollowRequestResponse(
    Long requestId,
    Long requesterId,
    Long recipientId,
    FollowRequestStatus status
) {
    public static FollowRequestResponse from(FollowHistory request) {
        return new FollowRequestResponse(
            request.getId(),
            request.getRequesterId(),
            request.getRecipientId(),
            request.getFollowRequestStatus()
        );
    }
}
