package com.all_i.allibe.member.dto.request;

public record NoneSocialMemberUpdateRequest(
        String nickname,
        String description,
        boolean noticeCheck,
        boolean darkModeCheck,
        String password
) {
}
