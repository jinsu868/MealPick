package com.all_i.allibe.follow.dto;

public record FollowMemberInfo(
    Long followerId,
    String imageUrl,
    String nickName
){
    public static FollowMemberInfo of(
        Long followingId,
        String imageUrl,
        String nickName
    ) {
        return new FollowMemberInfo(
                followingId,
                imageUrl,
                nickName
        );
    }
}
