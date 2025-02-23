package com.all_i.allibe.follow.dto.followrequest;

import com.all_i.allibe.follow.followrequest.domain.FollowHistory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FollowStatusRequest(
    @NotNull @Min(1)
    Long requesterId,
    @NotNull
    FollowHistory.FollowRequestStatus status
){}
