package com.all_i.allibe.post.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum MealTime {
    MORNING("아침"),
    LUNCH("점심"),
    DINNER("저녁");

    private final String key;
}
