package com.all_i.allibe.follow.dto.followrequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FollowRequest(
    @NotNull @Min(1)
    Long recipientId
) {}
