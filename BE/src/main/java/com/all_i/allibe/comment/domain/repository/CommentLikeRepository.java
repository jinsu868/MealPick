package com.all_i.allibe.comment.domain.repository;

import com.all_i.allibe.comment.domain.CommentLike;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long>, CommentLikeRepositoryCustom {

    boolean existsByMemberIdAndCommentId(
            Long memberId,
            Long commentId
    );

    Optional<CommentLike> findByMemberIdAndCommentId(
            Long memberId,
            Long commentId
    );

    List<CommentLike> findAllCommentIdByMemberId(Long memberId);

    List<CommentLike> findAllByMemberIdAndCommentIdIn(Long memberId, List<Long> commentIds);
}
