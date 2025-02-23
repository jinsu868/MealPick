package com.all_i.allibe.member.domain.repository;

import com.all_i.allibe.member.dto.response.MemberSearchResponse;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberSearchResponse> searchMembers(
            Long memberId,
            String keyword
    );
}
