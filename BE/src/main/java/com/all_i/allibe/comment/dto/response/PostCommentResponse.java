package com.all_i.allibe.comment.dto.response;

import java.time.LocalDateTime;

public record PostCommentResponse (
        Long id,
        Long authorId,
        String nickName,
        String profileImageUrl,
        String content,
        boolean isLiked,
        boolean deleted,
        int commentCount,
        int likeCount,
        LocalDateTime createdAt
) {
}
