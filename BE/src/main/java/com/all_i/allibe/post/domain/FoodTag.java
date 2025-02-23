package com.all_i.allibe.post.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum FoodTag {
    KOREAN("한식"),
    WESTERN("양식"),
    JAPANESE("일식"),
    CHINESE("중식"),
    BUNSIK("분식"),
    ASIAN("아시안"),
    FASTFOOD("패스트푸드"),
    CHICKEN("치킨"),
    PIZZA("피자"),
    DESSERT("디저트");

    private String key;
}
