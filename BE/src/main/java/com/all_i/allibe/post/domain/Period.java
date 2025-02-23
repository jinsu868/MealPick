package com.all_i.allibe.post.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum Period {
    MONTH(30),
    TWO_MONTHS(60),
    QUARTER(90),
    YEAR(365);

    private long key;
}
