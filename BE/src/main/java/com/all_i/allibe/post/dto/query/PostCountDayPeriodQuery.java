package com.all_i.allibe.post.dto.query;

public record PostCountDayPeriodQuery(
        String date,
        String tagName,
        int count
) {
}
