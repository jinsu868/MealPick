package com.all_i.allibe.comment.application;

import static com.all_i.allibe.common.exception.ErrorCode.COMMENT_LIKE_NOT_FOUND;
import static com.all_i.allibe.common.exception.ErrorCode.COMMENT_NOT_FOUND;
import static com.all_i.allibe.common.exception.ErrorCode.DUPLICATE_COMMENT_LIKE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import com.all_i.allibe.comment.domain.CommentLike;
import com.all_i.allibe.comment.domain.repository.CommentLikeRepository;
import com.all_i.allibe.comment.domain.repository.CommentRepository;
import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.member.domain.Member;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceTest {

    @InjectMocks
    CommentLikeService commentLikeService;

    @Mock
    CommentLikeRepository commentLikeRepository;

    @Mock
    CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 좋아요를 할 수 있다.")
    public void can_like_comment() {
        Long memberId = 1L;
        Long commentId = 1L;
        Member member = Member.of(memberId, "test_social_id", "test_nickname", "test_profile_image");

        CommentLike commentLike = CommentLike.createCommentLike(memberId, commentId);

        given(commentRepository.existsById(1L)).willReturn(true);
        given(commentLikeRepository.existsByMemberIdAndCommentId(memberId, commentId)).willReturn(false);
        given(commentLikeRepository.save(any())).willReturn(commentLike);

        commentLikeService.like(commentId, member);

        verify(commentLikeRepository).save(any());
        verify(commentRepository).existsById(any());
        verify(commentLikeRepository).existsByMemberIdAndCommentId(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 댓글에 좋아요할 수 없다.")
    public void can_not_like_at_none_exist_comment() {
        Long memberId = 1L;
        Long commentId = 1L;
        Member member = Member.of(memberId, "test_social_id", "test_nickname", "test_profile_image");

        given(commentRepository.existsById(any())).willReturn(false);

        Assertions.assertThatThrownBy(() -> commentLikeService.like(commentId, member))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(COMMENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("같은 유저가 중복해서 같은 댓글에 좋아요할 수 없다.")
    public void can_not_duplicate_like_at_comment() {
        Long memberId = 1L;
        Long commentId = 1L;
        Member member = Member.of(memberId, "test_social_id", "test_nickname", "test_profile_image");

        given(commentRepository.existsById(any())).willReturn(true);
        given(commentLikeRepository.existsByMemberIdAndCommentId(memberId, commentId)).willReturn(true);

        Assertions.assertThatThrownBy(() -> commentLikeService.like(commentId, member))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DUPLICATE_COMMENT_LIKE.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 댓글에 대해서 좋아요 취소할 수 없다.")
    public void can_not_cancel_like_at_none_exist_comment() {
        Long memberId = 1L;
        Long commentId = 1L;
        Member member = Member.of(memberId, "test_social_id", "test_nickname", "test_profile_image");

        given(commentRepository.existsById(any())).willReturn(false);

        Assertions.assertThatThrownBy(() -> commentLikeService.cancel(commentId, member))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(COMMENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("좋아요하지 않은 댓글에 대해서 좋아요 취소할 수 없다.")
    public void can_not_cancel_like_at_comment_that_not_like() {
        Long memberId = 1L;
        Long commentId = 1L;
        Member member = Member.of(memberId, "test_social_id", "test_nickname", "test_profile_image");

        given(commentRepository.existsById(any())).willReturn(true);
        given(commentLikeRepository.findByMemberIdAndCommentId(memberId, commentId)).willReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> commentLikeService.cancel(commentId, member))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(COMMENT_LIKE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("좋아요를 취소할 수 있다.")
    public void can_cancel_like_at_comment() {
        Long memberId = 1L;
        Long commentId = 1L;
        Member member = Member.of(memberId, "test_social_id", "test_nickname", "test_profile_image");
        CommentLike commentLike = CommentLike.createCommentLike(memberId, commentId);

        given(commentRepository.existsById(any())).willReturn(true);
        given(commentLikeRepository.findByMemberIdAndCommentId(memberId, commentId)).willReturn(Optional.of(commentLike));
        doNothing().when(commentLikeRepository).delete(any());

        commentLikeService.cancel(commentId, member);

        verify(commentLikeRepository).delete(any());
        verify(commentRepository).existsById(any());
        verify(commentLikeRepository).findByMemberIdAndCommentId(any(), any());
    }
}