//package com.all_i.allibe.comment.application;
//
//import static com.all_i.allibe.common.exception.ErrorCode.NOT_MY_COMMENT;
//import static com.all_i.allibe.common.exception.ErrorCode.NO_MORE_COMMENT;
//import static com.all_i.allibe.common.exception.ErrorCode.POST_NOT_FOUND;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//
//import com.all_i.allibe.comment.domain.Comment;
//import com.all_i.allibe.comment.domain.repository.CommentRepository;
//import com.all_i.allibe.comment.dto.request.ChildCommentCreateRequest;
//import com.all_i.allibe.comment.dto.request.CommentUpdateRequest;
//import com.all_i.allibe.comment.dto.request.RootCommentCreateRequest;
//import com.all_i.allibe.common.exception.BadRequestException;
//import com.all_i.allibe.member.domain.Member;
//
//import java.util.Optional;
//
//import com.all_i.allibe.post.domain.repository.PostRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//public class CommentServiceTest {
//
//    @InjectMocks
//    private CommentService commentService;
//
//    @Mock
//    CommentRepository commentRepository;
//
//    @Mock
//    PostRepository postRepository;
//
//    @Test
//    @DisplayName("루트 댓글을 생성할 수 있다.")
//    public void can_create_root_comment() {
//        Long postId = 1L;
//        Member member = Member.of("123", "test_nickname", "test_profile_image");
//        Comment comment = Comment.createComment(postId, member.getId(), "test_conent");
//        var request = new RootCommentCreateRequest("test_content");
//
//        given(postRepository.existsById(any()))
//                .willReturn(true);
//
//        given(commentRepository.save(any()))
//                .willReturn(comment);
//
//        commentService.createRootComment(postId, request, member);
//
//        verify(postRepository).existsById(any());
//        verify(commentRepository).save(any());
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 게시글에 댓글을 생성할 수 없다.")
//    public void if_not_exist_post_then_can_not_create_post() {
//        Long postId = 1L;
//        Member member = Member.of("123", "test_nickname", "test_profile_image");
//        var request = new RootCommentCreateRequest("test_content");
//
//        given(postRepository.existsById(any()))
//                .willReturn(false);
//
//        Assertions.assertThatThrownBy(() -> commentService.createRootComment(postId, request, member))
//                .hasMessage(POST_NOT_FOUND.getMessage())
//                .isInstanceOf(BadRequestException.class);
//    }
//
//    @Test
//    @DisplayName("대댓글을 생성할 수 있다.")
//    public void can_create_child_comment() {
//        Long postId = 1L;
//        Long rootCommentId = 1L;
//        Member member = Member.of("123", "test_nickname", "test_profile_image");
//        Comment comment = Comment.createComment(postId, member.getId(), rootCommentId, "test_conent");
//        Comment rootComment = Comment.createComment(postId, member.getId(), "root_conent");
//        var request = new ChildCommentCreateRequest("test_contnet");
//
//        given(commentRepository.findById(any()))
//                .willReturn(Optional.of(rootComment));
//
//        given(commentRepository.save(any()))
//                .willReturn(comment);
//
//        commentService.createChildComment(rootCommentId, request, member);
//
//        verify(commentRepository).findById(any());
//        verify(commentRepository).save(any());
//    }
//
//    @Test
//    @DisplayName("대댓글의 대댓글은 작성할 수 없다.")
//    public void can_not_comment_at_not_parent_comment() {
//        Long postId = 1L;
//        Long rootCommentId = 1L;
//        Member member = Member.of("123", "test_nickname", "test_profile_image");
//        Comment comment = Comment.createComment(postId, member.getId(), rootCommentId, "test_conent");
//        var request = new ChildCommentCreateRequest("test_contnet");
//
//        given(commentRepository.findById(any()))
//                .willReturn(Optional.of(comment));
//
//        Assertions.assertThatThrownBy(() -> commentService.createChildComment(rootCommentId, request, member))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(NO_MORE_COMMENT.getMessage());
//    }
//
//    @Test
//    @DisplayName("댓글 생성자가 아니면 댓글을 삭제할 수 없다.")
//    public void if_not_owner_can_not_delete_comment() {
//        Long postId = 1L;
//        Long rootCommentId = 1L;
//        Member member = Member.of(1L, "123", "test_nickname", "test_profile_image");
//        Comment comment = Comment.createComment(postId, 12345L, rootCommentId, "test_conent");
//
//        given(commentRepository.findById(any()))
//                .willReturn(Optional.of(comment));
//
//        Assertions.assertThatThrownBy(() -> commentService.deleteComment(1L, member))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(NOT_MY_COMMENT.getMessage());
//    }
//
//    @Test
//    @DisplayName("댓글 생성자는 댓글을 삭제할 수 있다.")
//    public void if_owner_can_delete_comment() {
//        Long postId = 1L;
//        Long rootCommentId = 1L;
//        Member member = Member.of(1L, "123", "test_nickname", "test_profile_image");
//        Comment comment = Comment.createComment(postId, member.getId(), rootCommentId, "test_conent");
//
//        given(commentRepository.findById(any()))
//                .willReturn(Optional.of(comment));
//
//        commentService.deleteComment(comment.getId(), member);
//
//        verify(commentRepository).findById(any());
//        verify(commentRepository).delete(any());
//    }
//
//    @Test
//    @DisplayName("존재하지 않는(삭제된) 게시글의 댓글은 읽을 수 없다.")
//    public void if_not_exist_post_then_can_not_read_comment() {
//        Long postId = 1L;
//        Member member = Member.of(1L, "123", "test_nickname", "test_profile_image");
//        String pageToken = "1";
//
//        given(postRepository.existsById(any()))
//                .willReturn(false);
//
//        Assertions.assertThatThrownBy(() -> commentService.findAllRootComment(postId, member, pageToken))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(POST_NOT_FOUND.getMessage());
//    }
//
//    @Test
//    @DisplayName("댓글 작성자가 아니면 댓글을 수정할 수 없다.")
//    public void if_not_owner_then_can_not_update_comment() {
//        Long postId = 1L;
//        Long rootCommentId = 1L;
//        Member member = Member.of(1L, "123", "test_nickname", "test_profile_image");
//        Comment comment = Comment.createComment(postId, 12345L, rootCommentId, "test_conent");
//        var request = new CommentUpdateRequest("update_test_comment");
//
//        given(commentRepository.findById(any()))
//                .willReturn(Optional.of(comment));
//
//        Assertions.assertThatThrownBy(() -> commentService.updateComment(1L, member, request))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(NOT_MY_COMMENT.getMessage());
//    }
//
//    @Test
//    @DisplayName("댓글 작성자는 댓글을 수정할 수 있다.")
//    public void if_owner_then_can_updagte_comment() {
//        String updatedContent = "update_test_content";
//        Long postId = 1L;
//        Long rootCommentId = 1L;
//        Member member = Member.of(1L, "123", "test_nickname", "test_profile_image");
//        Comment comment = Comment.createComment(postId, member.getId(), rootCommentId, "test_conent");
//        var request = new CommentUpdateRequest(updatedContent);
//
//        given(commentRepository.findById(any()))
//                .willReturn(Optional.of(comment));
//
//        commentService.updateComment(comment.getId(), member, request);
//
//        Assertions.assertThat(comment.getContent()).isEqualTo(updatedContent);
//    }
//
//    @Test
//    @DisplayName("대댓글의 대댓글은 읽을 수 없다.")
//    public void can_not_read_second_level_child_comment() {
//        Long postId = 1L;
//        Long rootCommentId = 1L;
//        Member member = Member.of(1L, "123", "test_nickname", "test_profile_image");
//        Comment comment = Comment.createComment(postId, member.getId(), rootCommentId, "test_conent");
//
//        given(commentRepository.findById(any()))
//                .willReturn(Optional.of(comment));
//
//        Assertions.assertThatThrownBy(() -> commentService.findAllChildComment(1L, null))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(NO_MORE_COMMENT.getMessage());
//    }
//}