package com.all_i.allibe.follow.presentation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.all_i.allibe.common.controller.ControllerTest;
import com.all_i.allibe.common.dto.PageInfo;
import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.common.exception.ErrorCode;
import com.all_i.allibe.follow.application.FollowService;
import com.all_i.allibe.follow.domain.Follow;
import com.all_i.allibe.follow.domain.repository.FollowRepository;
import com.all_i.allibe.follow.dto.FollowInfo;
import com.all_i.allibe.follow.dto.FollowMemberInfo;
import com.all_i.allibe.follow.dto.FollowResponse;
import com.all_i.allibe.follow.dto.FollowingMemberInfo;
import com.all_i.allibe.follow.dto.followrequest.FollowRequest;
import com.all_i.allibe.follow.dto.followrequest.FollowRequestResponse;
import com.all_i.allibe.follow.dto.followrequest.FollowStatusRequest;
import com.all_i.allibe.follow.followrequest.domain.FollowHistory;
import com.all_i.allibe.follow.followrequest.domain.FollowHistory.FollowRequestStatus;
import com.all_i.allibe.member.dto.response.MemberDetailResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureRestDocs
@WebMvcTest(FollowController.class)
class FollowControllerTest extends ControllerTest {

    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String PAGE_TOKEN = "page-token";

    @MockitoBean
    FollowService followService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("팔로우를 요청한다.")
    void follow_request() throws Exception {
        var followRequest = new FollowRequest(1L);
        var followHistory = FollowHistory.createRequest(1L, 1L);
        var expect = FollowRequestResponse.from(followHistory);

        when(followService.requestFollow(any(), any()))
                .thenReturn(expect);

        MvcResult result = mockMvc.perform(post("/api/v1/follow/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .cookie(new Cookie(REFRESH_TOKEN, REFRESH_TOKEN))
                        .content(objectMapper.writeValueAsString(followRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        FollowRequestResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                FollowRequestResponse.class
        );

        verify(followService).requestFollow(any(), any());

        Assertions.assertThat(response).isEqualTo(expect);
    }

    @Test
    @DisplayName("팔로우 요청을 처리한다.")
    void handle_follow_request() throws Exception {
        var request = new FollowStatusRequest(1L, FollowRequestStatus.ACCEPT);
        var followHistory = FollowHistory.createRequest(1L, 1L);
        var expect = FollowRequestResponse.from(followHistory);

        when(followService.handleFollow(any(), any(), any()))
                .thenReturn(expect);

        MvcResult result = mockMvc.perform(patch("/api/v1/follow/request/handle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .cookie(new Cookie(REFRESH_TOKEN, REFRESH_TOKEN))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(followService).handleFollow(any(), any(), any());

        FollowRequestResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                FollowRequestResponse.class
        );

        Assertions.assertThat(response).isEqualTo(expect);
    }

    @Test
    @DisplayName("나의 팔로워를 조회한다")
    void testFetchFollowers() throws Exception {
        // given
        PageInfo<FollowMemberInfo> expectedResponse = PageInfo.of(
                "next-page-token",
                Arrays.asList(
                        FollowMemberInfo.of(2L,"profileImage2","nickname2"),
                        FollowMemberInfo.of(3L,"profileImage3","nickname3")
                ),
                true
        );

        when(followService.fetchFollowers(
                        any(),
                        any()
                ))
                .thenReturn(expectedResponse);

        // when & then
        MvcResult result = mockMvc.perform(get("/api/v1/follow/me/follower")
                        .param("pageToken", PAGE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .cookie(new Cookie(REFRESH_TOKEN, REFRESH_TOKEN)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageInfo<FollowMemberInfo>>() {}
        );

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("내가 팔로우하는 계정을 조회한다")
    void testFetchFollows() throws Exception {
        // given
        PageInfo<FollowingMemberInfo> expectedResponse = PageInfo.of(
                "next-page-token",
                Arrays.asList(
                        FollowingMemberInfo.of(2L,"profileImage2","nickname2"),
                        FollowingMemberInfo.of(3L,"profileImage3","nickname3")
                ),
                true
        );

        when(followService.fetchFollows(
                        any(),
                        any()
                ))
                .thenReturn(expectedResponse);

        // when & then
        MvcResult result = mockMvc.perform(get("/api/v1/follow/me/following")
                        .param("pageToken", PAGE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .cookie(new Cookie(REFRESH_TOKEN, REFRESH_TOKEN)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageInfo<FollowingMemberInfo>>() {}
        );

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("날 팔로우한 유저를 조회하다")
    void testFetchFollowRequest() throws Exception {
        // given
        PageInfo<FollowMemberInfo> expectedResponse = PageInfo.of(
                "next-page-token",
                Arrays.asList(
                        FollowMemberInfo.of(2L,"profileImage2","nickname2"),
                        FollowMemberInfo.of(3L,"profileImage3","nickname3")
                ),
                true
        );

        when(followService.fetchFollowRequest(
                any(),
                any()
        ))
                .thenReturn(expectedResponse);

        // when & then
        MvcResult result = mockMvc.perform(get("/api/v1/follow/stand-by")
                        .param("pageToken", PAGE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .cookie(new Cookie(REFRESH_TOKEN, REFRESH_TOKEN)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageInfo<FollowMemberInfo>>() {}
        );

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("팔로우한 유저를 언팔로우한다")
    void testUnfollow() throws Exception {
        // given
        Long recipientId = 2L;

        doNothing()
                .when(followService)
                .unFollow(any(),any());

        // when & then
        mockMvc.perform(delete("/api/v1/follow/{recipientId}",recipientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .cookie(new Cookie(REFRESH_TOKEN, REFRESH_TOKEN)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(followService).unFollow(any(),eq(recipientId));
    }
}