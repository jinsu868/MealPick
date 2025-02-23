package com.all_i.allibe.member.dto;


import com.all_i.allibe.member.domain.Member;

public record MemberResponse (
    Long memberId,
    String name,
    String nickName,
    String profileImage,
    String alias
){

    public static MemberResponse from(Member member) {
        return new MemberResponse(
            member.getId(),
            member.getName(),
            member.getNickname(),
            member.getProfileImage(),
            member.getAlias()
        );
    }
}
