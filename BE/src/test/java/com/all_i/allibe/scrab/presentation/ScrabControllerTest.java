package com.all_i.allibe.scrab.presentation;

import com.all_i.allibe.common.controller.ControllerTest;
import com.all_i.allibe.post.domain.FoodTag;
import com.all_i.allibe.post.domain.MealTime;
import com.all_i.allibe.scrab.application.ScrabService;
import com.all_i.allibe.scrab.dto.response.ScrabResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ScrabController.class)
class ScrabControllerTest extends ControllerTest {

    @MockitoBean
    ScrabService scrabService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("게시물을 스크랩 한다.")
    public void test_create_scrab() throws Exception {
        Long scrabId = 1L;
        Long postId = 1L;

        given(scrabService.createScrab(any(), any()))
                .willReturn(1L);

        mockMvc.perform(post("/api/v1/posts/" + postId + "/scrab")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scrabId)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("스크랩한 게시글들을 불러옵니다.")
    public void test_get_scrabs() throws Exception {

        List<String> tags = List.of("tag1", "tag2", "tag3");

        var expect = List.of(
                new ScrabResponse(1L, "test1", "url1", FoodTag.ASIAN, MealTime.LUNCH, tags),
                new ScrabResponse(2L, "test2", "url2", FoodTag.ASIAN, MealTime.LUNCH, tags),
                new ScrabResponse(3L, "test3", "url3", FoodTag.ASIAN, MealTime.LUNCH, tags)
        );

        given(scrabService.getScrabs(any()))
                .willReturn(expect);

        MvcResult result = mockMvc.perform(get("/api/v1/scrabs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<ScrabResponse> actualScrabs = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, ScrabResponse.class)
        );

        assertEquals(expect.size(), actualScrabs.size());
        assertIterableEquals(expect, actualScrabs);
    }

    @Test
    @DisplayName("스크랩을 삭제합니다.")
    public void test_delete_scrab() throws Exception {
        Long postId = 1L;
        doNothing().when(scrabService).deleteScrab(any());

        mockMvc.perform(delete("/api/v1/scrabs/" + postId))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(scrabService).deleteScrab(any());
    }
}