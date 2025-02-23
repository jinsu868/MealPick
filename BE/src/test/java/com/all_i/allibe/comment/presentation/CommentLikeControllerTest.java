package com.all_i.allibe.comment.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.all_i.allibe.comment.application.CommentLikeService;
import com.all_i.allibe.common.controller.ControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@AutoConfigureRestDocs
@WebMvcTest(CommentLikeController.class)
class CommentLikeControllerTest extends ControllerTest {

    @MockitoBean
    CommentLikeService commentLikeService;

    @Test
    @DisplayName("댓글 좋아요를 할 수 있다.")
    public void can_create_comment_like() throws Exception {
        Long commentId = 1L;

        given(commentLikeService.like(any(), any()))
                .willReturn(1L);

        mockMvc.perform(post("/api/v1/comments/" + commentId + "/like"))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(commentLikeService).like(any(), any());
    }

    @Test
    @DisplayName("댓글 좋아요를 취소할 수 있다.")
    public void can_cancel_comment_like() throws Exception {
        Long commentId = 1L;

        doNothing().when(commentLikeService).cancel(any(), any());

        mockMvc.perform(delete("/api/v1/comments/" + commentId + "/cancel-like"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(commentLikeService).cancel(any(), any());
    }
}

