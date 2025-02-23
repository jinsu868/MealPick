package com.all_i.allibe.post.dto.response;

import com.all_i.allibe.post.domain.FoodTag;

import java.time.LocalDateTime;
import java.util.List;

public record AlbumResponse(
        FoodTag foodTag,
        LocalDateTime lastCreatedAt,
        int postCount,
        List<String> postImages
) {
}
