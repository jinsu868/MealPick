package com.all_i.allibe.follow.dto;

public record FollowInfo(
        Long followerId
){

    public static FollowInfo from(Long followingId) {
        return new FollowInfo(followingId);
    }
}
