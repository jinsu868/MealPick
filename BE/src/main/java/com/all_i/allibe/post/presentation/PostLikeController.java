package com.all_i.allibe.post.presentation;

import com.all_i.allibe.auth.presentation.annotation.AuthMember;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.post.application.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> createPostLike(
            @AuthMember Member member,
            @PathVariable(name = "postId") Long postId
    ) {
        Long postLikeId = postLikeService.createPostLike(postId, member);
        return ResponseEntity.created(URI.create("/api/v1/postLikes/" + postLikeId))
                .build();
    }

    @DeleteMapping("/{postId}/post-like")
    public ResponseEntity<Void> deletePostLike(
            @AuthMember Member member,
            @PathVariable(name = "postId") Long postId
    ) {
        postLikeService.deletePostLike(postId, member);
        return ResponseEntity.noContent().build();
    }
}
