package com.all_i.allibe.post.dto.query;

import com.all_i.allibe.post.domain.FoodTag;
import com.all_i.allibe.post.domain.MealTime;

import java.time.LocalDateTime;
import java.util.List;

public record PostInfoQuery(
        Long postId,
        List<String> postImages,
        List<String> tags,
        LocalDateTime createdAt,
        Long authorId,
        String authorImageUrl,
        String authorNickname,
        String title,
        String content,
        MealTime mealTime,
        FoodTag foodTag
) {
}
