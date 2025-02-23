package com.all_i.allibe.post.dto.request;

import com.all_i.allibe.post.domain.FoodTag;

import java.time.LocalDateTime;
import java.util.List;

public record PostCreateRequest(
        String title,
        String content,
        FoodTag foodTag,
        Double latitude,
        Double longitude,
        List<String> tags,
        LocalDateTime requestTime
) {
}
