package com.all_i.allibe.comment.application;

import static com.all_i.allibe.common.exception.ErrorCode.*;

import com.all_i.allibe.comment.domain.CommentLike;
import com.all_i.allibe.comment.domain.repository.CommentLikeRepository;
import com.all_i.allibe.comment.domain.repository.CommentRepository;
import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    public Long like(
            Long commentId,
            Member loginMember
    ) {
        if (!commentRepository.existsById(commentId)) {
            throw new BadRequestException(COMMENT_NOT_FOUND);
        }

        if (commentLikeRepository.existsByMemberIdAndCommentId(loginMember.getId(), commentId)) {
            throw new BadRequestException(DUPLICATE_COMMENT_LIKE);
        }

        var commentLike = CommentLike.createCommentLike(loginMember.getId(), commentId);
        return commentLikeRepository.save(commentLike).getId();
    }

    public void cancel(
            Long commentId,
            Member loginMember
    ) {
        if (!commentRepository.existsById(commentId)) {
            throw new BadRequestException(COMMENT_NOT_FOUND);
        }

        CommentLike commentLike = findCommentLike(loginMember.getId(), commentId);
        commentLikeRepository.delete(commentLike);
    }

    private CommentLike findCommentLike(Long memberId, Long commentId) {
        return commentLikeRepository.findByMemberIdAndCommentId(memberId, commentId)
                .orElseThrow(() -> new BadRequestException(COMMENT_LIKE_NOT_FOUND));
    }
}
