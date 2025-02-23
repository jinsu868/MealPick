package com.all_i.allibe.post.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.all_i.allibe.common.controller.ControllerTest;
import com.all_i.allibe.post.application.TagService;
import com.all_i.allibe.post.dto.response.PostCountDayPeriodResponse;
import com.all_i.allibe.post.dto.response.RookieMenuResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureRestDocs
@WebMvcTest(TagController.class)
class TagControllerTest extends ControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TagService tagService;

    @Test
    @DisplayName("태그에 해당하는 기간별 포스트 개수를 조회할 수 있다.")
    public void get_all_post_count_in_period() throws Exception {

        var expect = List.of(
                new PostCountDayPeriodResponse(
                        100L,
                        "1",
                        50
                ),
                new PostCountDayPeriodResponse(
                        30L,
                        "1",
                        22
                )
        );

        when(tagService.findPostCountInTag(any(), any()))
                .thenReturn(expect);

        MvcResult result = mockMvc.perform(get("/api/v1/tags/" + 1L + "/chart")
                        .param("period", "MONTH"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<PostCountDayPeriodResponse>>() {}
        );

        Assertions.assertThat(response).isEqualTo(expect);
    }

    @Test
    @DisplayName("최근 한달 가장 많이 증가한 태그 3개를 보여줍니다.")
    public void test_get_trending_tag_month() throws Exception {
        var expect = List.of(
                new RookieMenuResponse( "Vegan", 150),
                new RookieMenuResponse("Gluten-Free", 120),
                new RookieMenuResponse("Keto", 100)
        );

        when(tagService.findTopTrendingTagMonth())
                .thenReturn(expect);

        MvcResult result = mockMvc.perform(get("/api/v1/tags/trending"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<RookieMenuResponse>>() {}
        );

        Assertions.assertThat(response).isEqualTo(expect);
    }
}