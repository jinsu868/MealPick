package com.all_i.allibe.post.domain.repository;

import com.all_i.allibe.post.dto.query.PostLikeCountQuery;

import java.util.List;

public interface PostLikeRepositoryCustom {
     List<PostLikeCountQuery> getPostLikeCounts(List<Long> postIds);
     int countByPostId(Long postId);
}
