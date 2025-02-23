package com.all_i.allibe.common.dto;

import java.util.List;

public record PageInfo<T> (
        String pageToken,
        List<T> data,
        Boolean hasNext
) {
    public static <T> PageInfo<T> of(
            String pageToken,
            List<T> data,
            Boolean hasNext
    ) {
        return new PageInfo(
                pageToken,
                data,
                hasNext
        );
    }
}
