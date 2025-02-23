package com.all_i.allibe.member.dto.response;

public record MemberSearchResponse(
        Long memberId,
        String name,
        String nickname,
        String profileImageUrl,
        boolean isFollowed
) {
}
