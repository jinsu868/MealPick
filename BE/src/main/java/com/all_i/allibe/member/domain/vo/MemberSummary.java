package com.all_i.allibe.member.domain.vo;

public record MemberSummary(
        String name,
        String profileImage
) {
    public static MemberSummary of(
            String name,
            String profileImage
    ) {
        return new MemberSummary(
                name,
                profileImage
        );
    }
}
