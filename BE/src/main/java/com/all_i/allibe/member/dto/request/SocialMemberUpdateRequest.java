package com.all_i.allibe.member.dto.request;

public record SocialMemberUpdateRequest(
        String nickname,
        boolean noticeCheck,
        boolean darkModeCheck
) {
}
