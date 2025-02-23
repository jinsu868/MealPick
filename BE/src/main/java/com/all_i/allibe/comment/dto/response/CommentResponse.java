package com.all_i.allibe.comment.dto.response;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long authorId,
        String content,
        boolean deleted,
        int likeCount,
        LocalDateTime createdAt
) {
}
