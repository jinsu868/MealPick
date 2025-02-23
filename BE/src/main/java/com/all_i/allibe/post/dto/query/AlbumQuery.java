package com.all_i.allibe.post.dto.query;

import com.all_i.allibe.post.domain.FoodTag;

import java.time.LocalDateTime;

public record AlbumQuery(
        Long postId,
        FoodTag foodTag,
        LocalDateTime lastDate,
        int postCount,
        String postImage
) {
}
