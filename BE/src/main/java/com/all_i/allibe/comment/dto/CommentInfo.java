package com.all_i.allibe.comment.dto;

import java.time.LocalDateTime;

public record CommentInfo(
        Long id,
        Long authorId,
        String nickName,
        String profileImageUrl,
        String content,
        boolean deleted,
        LocalDateTime createdAt
) {
}
