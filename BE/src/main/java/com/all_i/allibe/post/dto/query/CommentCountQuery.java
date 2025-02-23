package com.all_i.allibe.post.dto.query;

public record CommentCountQuery(
        Long postId,
        Long commentCount
) {
}
