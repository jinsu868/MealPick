package com.all_i.allibe.comment.domain.repository;

import com.all_i.allibe.comment.domain.Comment;
import com.all_i.allibe.comment.dto.CommentInfo;
import com.all_i.allibe.comment.dto.response.CommentResponse;
import com.all_i.allibe.common.dto.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepository {
    private static final int COMMENT_PAGE_SIZE = 20;

    private final CommentJpaRepository commentJpaRepository;

    public List<CommentResponse> findAllRootCommentByPostId(
            Long postId,
            String pageToken
    ) {
        var data = commentJpaRepository.findAllRootComment(
                postId,
                pageToken,
                COMMENT_PAGE_SIZE
        );

        return data;
    }

    public PageInfo<CommentResponse> findAllChildCommentByParentCommentId(
            Long parentCommentId,
            String pageToken
    ) {
        var data = commentJpaRepository.findAllChildComment(
                parentCommentId,
                pageToken,
                COMMENT_PAGE_SIZE
        );

        if (data.size() <= COMMENT_PAGE_SIZE) {
            return PageInfo.of(null, data, false);
        }

        var lastData = data.get(data.size() - 1);
        data.remove(data.size() - 1);
        String nextPageToken = String.valueOf(lastData.id());

        return PageInfo.of(nextPageToken, data, true);
    }

    public PageInfo<CommentInfo> findAllChildCommentAndMemberByParentCommentId(
            Long parentCommentId,
            String pageToken
    ) {
        var data = commentJpaRepository.findAllChildCommentWithMember(
                parentCommentId,
                pageToken,
                COMMENT_PAGE_SIZE
        );

        if (data.size() <= COMMENT_PAGE_SIZE) {
            return PageInfo.of(null, data, false);
        }

        var lastData = data.get(data.size() - 1);
        data.remove(data.size() - 1);
        String nextPageToken = String.valueOf(lastData.id());

        return PageInfo.of(nextPageToken, data, true);
    }

    public int countByParentCommentId(Long parentCommentId) {
        return commentJpaRepository.countByParentCommentId(parentCommentId);
    }

    public int countByPostId(Long postId) {
        return commentJpaRepository.countByPostId(postId);
    }

    public Comment save(Comment comment) {
        return commentJpaRepository.save(comment);
    }

    public void delete(Comment comment) {
        commentJpaRepository.delete(comment);
    }

    public Optional<Comment> findById(Long commentId) {
        return commentJpaRepository.findById(commentId);
    }

    public boolean existsById(Long commentId) {
        return commentJpaRepository.existsById(commentId);
    }
}
