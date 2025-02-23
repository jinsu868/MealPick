package com.all_i.allibe.comment.presentation;

import com.all_i.allibe.auth.presentation.annotation.AuthMember;
import com.all_i.allibe.comment.application.CommentService;
import com.all_i.allibe.comment.dto.request.ChildCommentCreateRequest;
import com.all_i.allibe.comment.dto.request.CommentUpdateRequest;
import com.all_i.allibe.comment.dto.request.RootCommentCreateRequest;
import com.all_i.allibe.comment.dto.response.CommentResponse;
import com.all_i.allibe.comment.dto.response.PostCommentResponse;
import com.all_i.allibe.common.dto.PageInfo;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comment")
    public ResponseEntity<Void> createRootComment(
            @AuthMember Member member,
            @RequestBody RootCommentCreateRequest rootCommentCreateRequest,
            @PathVariable(name = "postId") Long postId
    ) {
        Long commentId = commentService.createRootComment(
                postId,
                rootCommentCreateRequest,
                member
        );
        return ResponseEntity.created(URI.create("/api/v1/posts/" + postId + "/comment/" + commentId))
                .build();
    }

    @PostMapping("/comments/{parentId}/write")
    public ResponseEntity<Void> createChildComment(
            @AuthMember Member member,
            @RequestBody ChildCommentCreateRequest childCommentCreateRequest,
            @PathVariable(name = "parentId") Long parentId
    ) {
        Long commentId = commentService.createChildComment(
                parentId,
                childCommentCreateRequest,
                member
        );
        return ResponseEntity.created(URI.create("/api/v1/comments/" + commentId + "/write"))
                .build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
        @AuthMember Member member,
        @PathVariable(name = "commentId") Long commentId
    ) {
        commentService.deleteComment(commentId, member);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/comments/{postId}/read")
    public ResponseEntity<PageInfo<PostCommentResponse>> findAllRootCommentInPost(
            @AuthMember Member member,
            @RequestParam(required = false) String pageToken,
            @PathVariable(name = "postId") Long postId
    ) {
        var response = commentService.findAllRootComment(postId, member, pageToken);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @AuthMember Member member,
            @PathVariable(name = "commentId") Long commentId,
            @RequestBody CommentUpdateRequest commentUpdateRequest
    ) {
        commentService.updateComment(commentId, member, commentUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/comments/{parentCommentId}/child-read")
    public ResponseEntity<PageInfo<PostCommentResponse>> findAllChildCommentInPost(
            @AuthMember Member member,
            @RequestParam(required = false) String pageToken,
            @PathVariable(name = "parentCommentId") Long parentCommentId
    ) {
        var response = commentService.findAllChildComment(parentCommentId, member, pageToken);
        return ResponseEntity.ok(response);
    }

}
