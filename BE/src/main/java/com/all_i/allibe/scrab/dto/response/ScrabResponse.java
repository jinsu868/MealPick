package com.all_i.allibe.scrab.dto.response;

import com.all_i.allibe.post.domain.FoodTag;
import com.all_i.allibe.post.domain.MealTime;
import com.all_i.allibe.scrab.dto.ScrabInfo;

import java.util.List;

public record ScrabResponse(
        Long postId,
        String title,
        String representImage,
        FoodTag foodTag,
        MealTime mealTime,
        List<String> tags
) {
    public static ScrabResponse from(ScrabInfo scrabInfo) {
        return new ScrabResponse(
                scrabInfo.postId(),
                scrabInfo.title(),
                scrabInfo.postImages().get(0),
                scrabInfo.foodTag(),
                scrabInfo.mealTime(),
                scrabInfo.tags()
        );
    }
}