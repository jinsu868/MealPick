package com.all_i.allibe.post.dto.query;

import java.util.List;

public record TagPicQuery(
        Long postId,
        List<String> postImages
) {
}
