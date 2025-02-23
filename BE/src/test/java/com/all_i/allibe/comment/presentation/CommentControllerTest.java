package com.all_i.allibe.comment.presentation;

import com.all_i.allibe.comment.application.CommentService;
import com.all_i.allibe.comment.dto.request.ChildCommentCreateRequest;
import com.all_i.allibe.comment.dto.request.CommentUpdateRequest;
import com.all_i.allibe.comment.dto.request.RootCommentCreateRequest;
import com.all_i.allibe.comment.dto.response.CommentResponse;
import com.all_i.allibe.comment.dto.response.PostCommentResponse;
import com.all_i.allibe.common.controller.ControllerTest;
import com.all_i.allibe.common.dto.PageInfo;
import com.all_i.allibe.member.domain.Member;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(CommentController.class)
class CommentControllerTest extends ControllerTest {

    @MockitoBean
    CommentService commentService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("게시글의 루트 댓글을 생성할 수 있다.")
    public void create_root_comment() throws Exception {
        Long postId = 1L;
        var request = new RootCommentCreateRequest("test_content");

        given(commentService.createRootComment(any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(post("/api/v1/posts/" + postId + "/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("대댓글을 작성할 수 있다.")
    public void create_child_comment() throws Exception {
        Long rootCommentId = 1L;
        var request = new ChildCommentCreateRequest("test_content");

        given(commentService.createChildComment(any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(post("/api/v1/comments/" + rootCommentId + "/write")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("댓글을 삭제할 수 있다.")
    public void delete_comment() throws Exception {
        Long commentId = 1L;
        doNothing().when(commentService).deleteComment(any(), any());

        mockMvc.perform(delete("/api/v1/comments/" + commentId))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(commentService).deleteComment(any(), any());
    }

    @Test
    @DisplayName("게시글 루트 댓글을 읽을 수 있다.")
    public void can_read_root_comment_in_post() throws Exception {
        Long postId = 1L;
        String pageToken = null;

        var data = List.of(
                new PostCommentResponse(1L, 1L, "author1", "profile1", "test_content_1", false, false, 5, 0, LocalDateTime.now()),
                new PostCommentResponse(2L, 2L, "author2", "profile2", "test_content_2", true, false, 4, 0, LocalDateTime.now()),
                new PostCommentResponse(3L, 3L, "author3", "profile3", "test_content_3", false, false, 3, 0, LocalDateTime.now())
        );
        PageInfo<PostCommentResponse> expect = PageInfo.of(pageToken, data, null);

        given(commentService.findAllRootComment(eq(postId), any(), eq(pageToken)))
                .willReturn(expect);

        MvcResult result = mockMvc.perform(get("/api/v1/comments/" + postId + "/read"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageInfo<PostCommentResponse>>() {}
        );

        Assertions.assertThat(response).isEqualTo(expect);
    }

    @Test
    @DisplayName("루트 댓글의 자식 댓글들을 읽을 수 있다.")
    public void can_read_child_comment() throws Exception {
        Long commentId = 1L;
        String pageToken = null;
        var data = List.of(
                new PostCommentResponse(1L, 1L, "", "", "test_content_1", false, false, 0, 5, LocalDateTime.now()),
                new PostCommentResponse(2L, 1L, "", "", "test_content_2", true, false, 0, 4, LocalDateTime.now()),
                new PostCommentResponse(3L, 1L, "", "", "test_content_3", false, false, 0, 3, LocalDateTime.now())
        );

        PageInfo<PostCommentResponse> expect = PageInfo.of(pageToken, data, null);

        given(commentService.findAllChildComment(any(), any(), any()))
                .willReturn(expect);
        MvcResult result = mockMvc.perform(get("/api/v1/comments/" + commentId + "/child-read"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageInfo<PostCommentResponse>>() {}
        );

        Assertions.assertThat(response).isEqualTo(expect);
    }

    @Test
    @DisplayName("댓글을 수정할 수 있다.")
    public void can_update_comment() throws Exception {
        var request = new CommentUpdateRequest("test_update_content");
        Long commentId = 1L;

        doNothing().when(commentService).updateComment(any(), any(), any());

        mockMvc.perform(patch("/api/v1/comments/" + commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(commentService).updateComment(any(), any(), any());
    }
}