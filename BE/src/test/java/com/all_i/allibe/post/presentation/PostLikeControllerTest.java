package com.all_i.allibe.post.presentation;

import com.all_i.allibe.common.controller.ControllerTest;
import com.all_i.allibe.post.application.PostLikeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@WebMvcTest(PostLikeController.class)
class PostLikeControllerTest extends ControllerTest {

    @MockitoBean
    PostLikeService postLikeService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("게시물을 좋아요한다.")
    void test_create_post_like() throws Exception {
        Long postId = 1L;

        given(postLikeService.createPostLike(any(), any()))
                .willReturn(1L);

        mockMvc.perform(post("/api/v1/posts/" + postId + "/like"))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(postLikeService).createPostLike(any(), any());
    }

    @Test
    @DisplayName("게시물 좋아요를 삭제할 수 있다.")
    public void test_cancel_post_like() throws Exception {
        Long postId = 1L;

        doNothing().when(postLikeService).deletePostLike(any(), any());

        mockMvc.perform(delete("/api/v1/posts/" + postId + "/post-like"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(postLikeService).deletePostLike(any(), any());
    }
}