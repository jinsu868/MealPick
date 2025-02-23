package com.all_i.allibe.post.dto.response;

public record PostCountDayPeriodResponse(
        Long daysAgo,
        String tagName,
        int count
) {
}
