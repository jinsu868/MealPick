package com.all_i.allibe.post.dto.response;

import com.all_i.allibe.post.domain.MealTime;

public record MemberMealTimeDataResponse(
        MealTime mealTime,
        int count
) {
}
