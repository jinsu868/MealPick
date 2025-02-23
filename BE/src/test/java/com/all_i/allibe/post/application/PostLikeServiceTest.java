//package com.all_i.allibe.post.application;
//
//import com.all_i.allibe.common.exception.BadRequestException;
//import com.all_i.allibe.member.domain.Member;
//import com.all_i.allibe.post.domain.PostLike;
//import com.all_i.allibe.post.domain.repository.PostLikeRepository;
//import com.all_i.allibe.post.domain.repository.PostRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static com.all_i.allibe.common.exception.ErrorCode.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//class PostLikeServiceTest {
//
//    @InjectMocks
//    PostLikeService postLikeService;
//
//    @Mock
//    PostLikeRepository postLikeRepository;
//
//    @Mock
//    PostRepository postRepository;
//
//    @Test
//    @DisplayName("게시물 좋아요를 할 수 있다.")
//    public void can_like_post() {
//        Long postId = 1L;
//        Long memberId = 1L;
//        Member member = Member.of(memberId, "test_social_id", "test_nickname", "test_profile_image");
//
//        PostLike postLike = PostLike.createPostLike(postId, memberId);
//
//        given(postRepository.existsById(1L)).willReturn(true);
//        given(postLikeRepository.existsByMemberIdAndPostId(memberId, postId)).willReturn(false);
//        given(postLikeRepository.save(any())).willReturn(postLike);
//
//        postLikeService.createPostLike(postId, member);
//
//        verify(postLikeRepository).save(any());
//        verify(postRepository).existsById(any());
//        verify(postLikeRepository).existsByMemberIdAndPostId(any(), any());
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 게시글에 좋아요할 수 없다.")
//    public void can_not_like_at_none_exist_comment() {
//        Long memberId = 1L;
//        Long postId = 1L;
//        Member member = Member.of(memberId, "test_social_id", "test_nickname", "test_profile_image");
//
//        given(postRepository.existsById(any())).willReturn(false);
//
//        Assertions.assertThatThrownBy(() -> postLikeService.createPostLike(postId, member))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(POST_NOT_FOUND.getMessage());
//    }
//
//    @Test
//    @DisplayName("같은 유저가 중복해서 같은 게시물에 좋아요할 수 없다.")
//    public void can_not_duplicate_like_at_post() {
//        Long postId = 1L;
//        Long memberId = 1L;
//        Member member = Member.of(memberId, "test_social_id", "test_nickname", "test_profile_image");
//
//        given(postRepository.existsById(any())).willReturn(true);
//        given(postLikeRepository.existsByMemberIdAndPostId(memberId, postId)).willReturn(true);
//
//        Assertions.assertThatThrownBy(() -> postLikeService.createPostLike(postId, member))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(DUPLICATE_POST_LIKE.getMessage());
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 게시글에 대해서 좋아요 취소할 수 없다.")
//    public void can_not_delete_like_at_none_exist_post() {
//        Long memberId = 1L;
//        Long postId = 1L;
//        Member member = Member.of(memberId, "test_social_id", "test_nickname", "test_profile_image");
//
//        given(postRepository.existsById(any())).willReturn(false);
//
//        Assertions.assertThatThrownBy(() -> postLikeService.deletePostLike(postId, member))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(POST_NOT_FOUND.getMessage());
//    }
//
//    @Test
//    @DisplayName("좋아요하지 않은 게시글에 대해서 좋아요 취소할 수 없다.")
//    public void can_not_delete_like_at_comment_that_not_like() {
//        Long memberId = 1L;
//        Long postId = 1L;
//        Member member = Member.of(memberId, "test_social_id", "test_nickname", "test_profile_image");
//
//        given(postRepository.existsById(any())).willReturn(true);
//        given(postLikeRepository.findByMemberIdAndPostId(memberId, postId)).willReturn(Optional.empty());
//
//        Assertions.assertThatThrownBy(() -> postLikeService.deletePostLike(postId, member))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(POST_LIKE_NOT_FOUND.getMessage());
//    }
//
//    @Test
//    @DisplayName("좋아요를 취소할 수 있다")
//    public void can_delete_like_at_post() {
//        Long memberId = 1L;
//        Long postId = 1L;
//        Member member = Member.of(memberId, "test_social_id", "test_nickname", "test_profile_image");
//        PostLike postLike = PostLike.createPostLike(postId, memberId);
//
//        given(postRepository.existsById(postId))
//                .willReturn(true);
//        given(postLikeRepository.findByMemberIdAndPostId(memberId, postId))
//                .willReturn(Optional.of(postLike));
//        doNothing().when(postLikeRepository).delete(any());
//
//        postLikeService.deletePostLike(postId, member);
//
//        verify(postRepository).existsById(postId);
//        verify(postLikeRepository).findByMemberIdAndPostId(memberId, postId);
//        verify(postLikeRepository).delete(postLike);
//    }
//}