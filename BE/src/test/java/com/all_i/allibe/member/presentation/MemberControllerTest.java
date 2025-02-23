package com.all_i.allibe.member.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.all_i.allibe.common.controller.ControllerTest;
import com.all_i.allibe.member.application.MemberService;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.member.dto.MemberResponse;
import com.all_i.allibe.member.dto.request.MemberAliasCreateRequest;
import com.all_i.allibe.member.dto.request.MemberLocationUpdateRequest;
import com.all_i.allibe.member.dto.request.SocialMemberUpdateRequest;
import com.all_i.allibe.member.dto.response.MemberDetailResponse;
import com.all_i.allibe.s3.application.S3FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureRestDocs
@WebMvcTest(MemberController.class)
class MemberControllerTest extends ControllerTest {

    @MockitoBean
    MemberService memberService;

    @MockitoBean
    S3FileService s3FileService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("나의 프로필 정보를 조회한다.")
    public void find_my_info() throws Exception {
        Member member = Member.of("test_social_id", "test_user_name", "profile_image");
        var expect = MemberDetailResponse.from(member);

        when(memberService.findMe(any()))
                .thenReturn(expect);

        MvcResult result = mockMvc.perform(get("/api/v1/members/me")
                        .header(HttpHeaders.AUTHORIZATION, "access-token")
                        .cookie(new Cookie("refresh-token", "refresh-token")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                MemberDetailResponse.class
        );

        Assertions.assertThat(response).usingRecursiveComparison().isEqualTo(expect);
    }

    @Test
    @DisplayName("다른 유저의 프로필을 조회한다.")
    void test_find_other_member_retrieve() throws Exception {
        Member member = Member.of("test_social_id", "test_user_name", "profile_image");
        var expect = MemberResponse.from(member);

        when(memberService.findMember(any()))
                .thenReturn(expect);

        MvcResult result = mockMvc.perform(get("/api/v1/members/" + 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "access-token")
                        .cookie(new Cookie("refresh-token", "refresh-token")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                MemberResponse.class
        );

        Assertions.assertThat(response).usingRecursiveComparison().isEqualTo(expect);
    }

    @Test
    @DisplayName("유저 정보를 수정할 수 있다.")
    void can_update_member() throws Exception {
        Member member = Member.of("test_social_id", "test_user_name", "profile_image");
        var data = new SocialMemberUpdateRequest(
                "change_nickname",
                true,
                false
        );

        MockMultipartFile request = new MockMultipartFile(
                "socialMemberUpdateRequest",
                "socialMemberUpdateRequest",
                APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(data)
        );

        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "profile.png",
                IMAGE_PNG_VALUE,
                "imageDummy".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(PATCH, "/api/v1/members/social")
                .file(request)
                .file(profileImage)
                .header(HttpHeaders.AUTHORIZATION, "access-token")
                .cookie(new Cookie("refresh-token", "refresh-token"))
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(memberService).updateSocialMember(any(), any(), any());
        verify(s3FileService).uploadFile(any(), any());
    }

    @Test
    @DisplayName("유저 별칭을 생성할 수 있다.")
    void create_alias() throws Exception {
        Member member = Member.of("test_social_id", "test_user_name", "profile_image");
        var request = new MemberAliasCreateRequest("test_alias");

        doNothing().when(memberService)
                .createAlias(any(), any());

        mockMvc.perform(post("/api/v1/members/alias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "access-token")
                        .cookie(new Cookie("refresh-token", "refresh-token")))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(memberService).createAlias(any(), any());
    }

    @Test
    @DisplayName("유저의 위치를 갱신할 수 있다.")
    void update_member_location() throws Exception {
        var request = new MemberLocationUpdateRequest(
                127.3232,
                32.1212
        );

        doNothing().when(memberService).updateLocation(any(), any());

        mockMvc.perform(patch("/api/v1/members/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(memberService).updateLocation(any(), any());
    }
}