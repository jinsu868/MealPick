package com.all_i.allibe.chat.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.all_i.allibe.chat.application.ChatRoomService;
import com.all_i.allibe.chat.dto.request.ChatRoomCreateRequest;
import com.all_i.allibe.chat.dto.response.ChatRoomCreateResponse;
import com.all_i.allibe.chat.dto.response.ChatRoomQueryResponse;
import com.all_i.allibe.chat.dto.response.ChatRoomResponse;
import com.all_i.allibe.comment.dto.response.PostCommentResponse;
import com.all_i.allibe.common.controller.ControllerTest;
import com.all_i.allibe.common.dto.PageInfo;
import com.all_i.allibe.member.dto.response.MemberDetailResponse;
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

@AutoConfigureRestDocs
@WebMvcTest(ChatRoomController.class)
class ChatRoomControllerTest extends ControllerTest {

    @MockitoBean
    ChatRoomService chatRoomService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("채팅방을 생성할 수 있다.")
    public void can_create_chat_room() throws Exception {
        var request = new ChatRoomCreateRequest("test_chat_room_name", 1L);
        var expect = ChatRoomCreateResponse.of(1L, true);

        when(chatRoomService.createChatRoom(any(), any()))
                .thenReturn(expect);

        MvcResult result = mockMvc.perform(post("/api/v1/chat-rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ChatRoomCreateResponse.class
        );

        Assertions.assertThat(response).isEqualTo(expect);


        verify(chatRoomService).createChatRoom(any(), any());
    }

    @Test
    @DisplayName("유저가 참여한 모든 채팅방을 조회한다.")
    public void retrieve_all_chat_rooms() throws Exception {
        String pageToken = "8";
        var data = List.of(
                new ChatRoomResponse(5L, "test_chat_room1", 15L, "test_sender", "test_content", LocalDateTime.now(), "test.png"),
                new ChatRoomResponse(1L, "test_chat_room2", 14L, "test_sender", "test_conent", LocalDateTime.now(), "test.png"),
                new ChatRoomResponse(2L, "test_chat_room3", 11L, "test_sender", "test_content", LocalDateTime.now(), "test.png")
        );


        when(chatRoomService.findAll(any()))
                .thenReturn(data);

        MvcResult result = mockMvc.perform(get("/api/v1/chat-rooms")
                        .param("pageToken", pageToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        var response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<List<ChatRoomResponse>>() {}
        );

        Assertions.assertThat(response).isEqualTo(data);
    }
}