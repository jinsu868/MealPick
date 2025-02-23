package com.all_i.allibe.follow.dto;

import com.all_i.allibe.follow.followrequest.domain.FollowHistory;

public record FollowStatusResponse (
        Long requesterId,
        Long recipientId,
        FollowHistory.FollowRequestStatus status
){
    public static FollowStatusResponse from(FollowHistory request) {
        return new FollowStatusResponse(
                request.getRequesterId(),
                request.getRecipientId(),
                request.getFollowRequestStatus()
        );
    }
}
