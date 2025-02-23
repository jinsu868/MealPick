package com.all_i.allibe.post.dto.response;

import com.all_i.allibe.post.domain.Post;

public record NearPostResponse(
        Long postId,
        String postImage
) {
    public static NearPostResponse from(Post post) {
        return new NearPostResponse(
                post.getId(),
                post.getPostImages().get(0)
        );
    }
}
