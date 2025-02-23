package com.all_i.allibe.scrab.dto;

import com.all_i.allibe.post.domain.FoodTag;
import com.all_i.allibe.post.domain.MealTime;

import java.util.List;

public record ScrabInfo(
        Long postId,
        String title,
        List<String> postImages,
        FoodTag foodTag,
        MealTime mealTime,
        List<String> tags
) {
}
