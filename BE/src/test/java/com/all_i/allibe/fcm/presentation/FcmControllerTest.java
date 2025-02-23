package com.all_i.allibe.fcm.presentation;

import com.all_i.allibe.common.controller.ControllerTest;
import com.all_i.allibe.fcm.application.FirebaseCloudMessageService;
import com.all_i.allibe.fcm.dto.FcmRequest;
import com.all_i.allibe.fcm.dto.FcmRequests;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FcmController.class)
@AutoConfigureRestDocs
class FcmControllerTest extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FirebaseCloudMessageService firebaseCloudMessageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("단일 대상에게 푸시 메시지를 성공적으로 전송한다")
    void sendPushMessageToSingleTarget() throws Exception {
        // given
        FcmRequest request = FcmRequest.builder()
                .targetToken("test-token")
                .title("Test Notification")
                .body("This is a test message")
                .build();

        willDoNothing()
                .given(firebaseCloudMessageService)
                .sendMessageTo(
                        request.getTargetToken(),
                        request.getTitle(),
                        request.getBody()
                );

        // when & then
        mockMvc.perform(post("/api/fcm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(content().string(""));

        verify(firebaseCloudMessageService).sendMessageTo(
                request.getTargetToken(),
                request.getTitle(),
                request.getBody()
        );
    }

    @Test
    @DisplayName("여러 사용자에게 푸시 메시지를 성공적으로 전송한다")
    void sendPushMessageToMultipleTargets() throws Exception {
        // given
        List<Long> memberIds = Arrays.asList(1L, 2L);
        FcmRequests request = FcmRequests.builder()
                .memberIds(memberIds)
                .title("Group Notification")
                .body("This is a group message")
                .build();

        doNothing().when(firebaseCloudMessageService)
                .sendMessageToMembers(
                        request.getMemberIds(),
                        request.getTitle(),
                        request.getBody()
                );

        // when & then
        mockMvc.perform(post("/api/fcm/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("fcm-multiple-send",
                        requestFields(
                                fieldWithPath("memberIds").description("수신자 ID 목록"),
                                fieldWithPath("title").description("알림 제목"),
                                fieldWithPath("body").description("알림 내용")
                        )));
        verify(firebaseCloudMessageService).sendMessageToMembers(
                request.getMemberIds(),
                request.getTitle(),
                request.getBody()
        );
    }

    @Test
    @DisplayName("잘못된 형식의 요청이 들어오면 400 에러를 반환한다")
    void invalidRequestShouldReturn400() throws Exception {
        // given
        FcmRequest invalidRequest = FcmRequest.builder()
                .targetToken("")
                .title("")
                .body("message")
                .build();

        // when & then
        mockMvc.perform(post("/api/fcm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}