package com.all_i.allibe.comment.presentation;

import com.all_i.allibe.auth.presentation.annotation.AuthMember;
import com.all_i.allibe.comment.application.CommentLikeService;
import com.all_i.allibe.member.domain.Member;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @AuthMember Member member,
            @PathVariable(name = "commentId") Long commentId
    ) {
        Long commentLikeId = commentLikeService.like(commentId, member);
        return ResponseEntity.created(URI.create("/api/v1/commentLikes/" + commentLikeId))
                .build();
    }

    @DeleteMapping("/{commentId}/cancel-like")
    public ResponseEntity<Void> cancelLike(
            @AuthMember Member member,
            @PathVariable(name = "commentId") Long commentId
    ) {
        commentLikeService.cancel(commentId, member);
        return ResponseEntity.noContent().build();
    }
}