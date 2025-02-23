package com.all_i.allibe.comment.domain.repository;

import com.all_i.allibe.comment.dto.CommentInfo;
import com.all_i.allibe.comment.dto.response.CommentResponse;

import java.util.List;

public interface CommentJpaRepositoryCustom {
    List<CommentResponse> findAllRootComment(
            Long postId,
            String pageToken,
            int pageSize
    );

    List<CommentResponse> findAllChildComment(
            Long parentCommentId,
            String pageToken,
            int pageSize
    );

    List<CommentInfo> findAllChildCommentWithMember(
            Long parentCommentId,
            String pageToken,
            int pageSize
    );

    int countByParentCommentId(Long parentCommentId);

    int countByPostId(Long postId);
}
