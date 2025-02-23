package com.all_i.allibe.post.dto.query;

public record PostLikeCountQuery(
        Long postId,
        Long likeCount
) {
}
