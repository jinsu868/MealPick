package com.all_i.allibe.post.dto.request;

import java.util.List;

public record PostUpdateRequest(
        String content,
        List<String> tags
) {
}
