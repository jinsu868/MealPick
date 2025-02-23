package com.all_i.allibe.member.dto.response;

import com.all_i.allibe.member.domain.Member;

public record MemberDetailResponse(
        Long id,
        String name,
        String nickname,
        String profileImage,
        String email,
        Boolean noticeCheck,
        Boolean darkModeCheck,
        String alias
) {
    public static MemberDetailResponse from(Member member) {
        return new MemberDetailResponse(
                member.getId(),
                member.getName(),
                member.getNickname(),
                member.getProfileImage(),
                member.getEmail(),
                member.getNoticeCheck(),
                member.getDarkModeCheck(),
                member.getAlias()
        );
    }
}
