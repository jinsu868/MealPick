package com.all_i.allibe.tagrank.dto;

import com.all_i.allibe.post.dto.response.TagResponse;
import java.time.LocalDateTime;
import java.util.List;

public record PostCreateEvent(
        Long postId,
        List<TagResponse> tags,
        LocalDateTime timestamp
) {
    public PostCreateEvent(
            Long postId,
            List<TagResponse> tagResponses
    ) {
        this(
            postId,
            tagResponses,
            LocalDateTime.now()
        );
    }
}
