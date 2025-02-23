package com.all_i.allibe.follow.dto;

import java.util.List;

public record FollowResponse(
        List<FollowInfo> followers
) {}
