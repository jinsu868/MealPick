package com.all_i.allibe.chat.application;

import static com.all_i.allibe.common.exception.ErrorCode.CHAT_ROOM_NOT_FOUND;
import static com.all_i.allibe.common.exception.ErrorCode.MEMBER_NOT_IN_CHAT_ROOM;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.all_i.allibe.chat.domain.ChatMember;
import com.all_i.allibe.chat.domain.repository.ChatMemberRepository;
import com.all_i.allibe.chat.domain.repository.ChatRepository;
import com.all_i.allibe.chat.domain.repository.ChatRoomRepository;
import com.all_i.allibe.chat.dto.request.ChatRequest;
import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.member.domain.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks
    ChatService chatService;

    @Mock
    ChatRepository chatRepository;

    @Mock
    ChatRoomRepository chatRoomRepository;

    @Mock
    ChatMemberRepository chatMemberRepository;

    @Mock
    MemberRepository memberRepository;

//    @Test
//    @DisplayName("roomId에 해당하는 채팅방이 없으면 실패한다.")
//    public void if_not_exist_chat_room_then_fail() {
//        var chatRequest = new ChatRequest("test_content", 1L);
//        Long senderId = 3L;
//        ChatMember chatSender = ChatMember.createChatMember(1L, 1L);
//        ChatMember chatReceiver = ChatMember.createChatMember(2L, 1L);
//
//        Member member = Member.of(senderId, "test_social_id", "test_nickname", "test_profile_image");
//
//        when(memberRepository.findById(any()))
//                .thenReturn(Optional.of(member));
//
//        when(chatRoomRepository.existsById(any()))
//                .thenReturn(false);
//
//        Assertions.assertThatThrownBy(() -> chatService.saveChat(chatRequest, chatRequest.roomId(), senderId))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(CHAT_ROOM_NOT_FOUND.getMessage());
//    }

//    @Test
//    @DisplayName("보내는 유저가 채팅방에 존재하지 않으면 실패한다.")
//    public void cant_send_message_if_sender_is_not_in_chat_room() {
//        var chatRequest = new ChatRequest("test_content", 1L);
//        Long senderId = 1L;
//
//        Member member = Member.of(senderId, "test_social_id", "test_nickname", "test_profile_image");
//
//        when(memberRepository.findById(any()))
//                .thenReturn(Optional.of(member));
//
//        when(chatRoomRepository.existsById(any()))
//                .thenReturn(true);
//
//        Assertions.assertThatThrownBy(() -> chatService.saveChat(chatRequest, chatRequest.roomId(), senderId))
//                .isInstanceOf(BadRequestException.class)
//                .hasMessage(MEMBER_NOT_IN_CHAT_ROOM.getMessage());
//    }

    @Test
    @DisplayName("없는 채팅방의 채팅을 조회할 수 없다.")
    public void can_not_retrieve_chat_if_not_exist_chat_room() {
        Long chatRoomId = 1L;
        Member member = Member.of(1L, "test_social_id", "test_nickname", "test_profile_image");
        String pageToken = null;

        when(chatRoomRepository.existsById(any()))
                .thenReturn(false);

        Assertions.assertThatThrownBy(() -> chatService.findAll(chatRoomId, member, pageToken))
                .hasMessage(CHAT_ROOM_NOT_FOUND.getMessage())
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("채팅방에 속해있지 않은 유저는 채팅을 조회할 수 없다.")
    public void can_not_retrieve_chat_if_not_in_chat_room() {
        Long chatRoomId = 1L;
        Member member = Member.of(1L, "test_social_id", "test_nickname", "test_profile_image");
        String pageToken = null;

        when(chatRoomRepository.existsById(any()))
                .thenReturn(true);

        when(chatMemberRepository.existsByChatRoomIdAndMemberId(any(), any()))
                .thenReturn(false);

        Assertions.assertThatThrownBy(() -> chatService.findAll(chatRoomId, member, pageToken))
                .hasMessage(MEMBER_NOT_IN_CHAT_ROOM.getMessage())
                .isInstanceOf(BadRequestException.class);
    }
}