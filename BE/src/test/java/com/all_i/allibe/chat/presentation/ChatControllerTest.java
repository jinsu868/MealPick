package com.all_i.allibe.chat.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.all_i.allibe.chat.application.ChatService;
import com.all_i.allibe.chat.dto.response.ChatRelayRequest;
import com.all_i.allibe.chat.dto.response.ChatResponse;
import com.all_i.allibe.common.controller.ControllerTest;
import com.all_i.allibe.common.dto.PageInfo;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureRestDocs
@WebMvcTest(ChatController.class)
class ChatControllerTest extends ControllerTest {

    @MockitoBean
    ChatService chatService;

    @MockitoBean
    SimpMessagingTemplate simpMessagingTemplate;

    @MockitoBean
    KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("채팅방에 속한 모든 채팅을 조회한다.")
    public void retrieve_chat_in_chat_room() throws Exception{
        Long chatRoomId = 1L;
        String pageToken = "7";

        var data = List.of(
                ChatResponse.of(4L, 1L, 1L, "test_profile_image1", LocalDateTime.now(), "test_content"),
                ChatResponse.of(5L, 1L, 1L,"test_profile_image2", LocalDateTime.now(), "test_content"),
                ChatResponse.of(6L, 1L, 1L, "test_profile_image3", LocalDateTime.now(), "test_content")
        );

        var expect = PageInfo.of(pageToken, data, true);

        when(chatService.findAll(any(), any(), any()))
                .thenReturn(expect);

        MvcResult result = mockMvc.perform(get("/api/v1/rooms/" + chatRoomId)
                        .param("pageToken", pageToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<PageInfo<ChatResponse>>() {}
        );

        Assertions.assertThat(response).isEqualTo(expect);
    }
}