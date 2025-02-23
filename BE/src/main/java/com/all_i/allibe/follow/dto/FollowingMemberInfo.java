package com.all_i.allibe.follow.dto;

public record FollowingMemberInfo(
    Long requestId,
    String imageUrl,
    String nickName
){
    public static FollowingMemberInfo of(
        Long requestId,
        String imageUrl,
        String nickName
    ) {
        return new FollowingMemberInfo(
                requestId,
                imageUrl,
                nickName
        );
    }
}
