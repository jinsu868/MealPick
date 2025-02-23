package com.all_i.allibe.post.dto.response;

public record TagResponse(
        String name
) {
    public static TagResponse from(String tag) {
        return new TagResponse(
                tag
        );
    }
}
