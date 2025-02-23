package com.all_i.allibe.follow.domain.repository;

import com.all_i.allibe.follow.domain.Follow;

import java.util.List;

public interface FollowRepositoryCustom {
    List<Follow> findFollowers(Long memberId, String pageToken, int limit);
    List<Follow> findFollows(Long memberId, String pageToken, int limit);
}
