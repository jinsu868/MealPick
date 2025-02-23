package com.all_i.allibe.chat.application;

import static com.all_i.allibe.common.exception.ErrorCode.CHATROOM_ALREADY_EXISTS;
import static com.all_i.allibe.common.exception.ErrorCode.MEMBER_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.all_i.allibe.chat.domain.ChatMember;
import com.all_i.allibe.chat.domain.repository.ChatMemberRepository;
import com.all_i.allibe.chat.domain.repository.ChatRoomRepository;
import com.all_i.allibe.chat.dto.request.ChatRoomCreateRequest;
import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.member.domain.Member;
import com.all_i.allibe.member.domain.repository.MemberRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @InjectMocks
    ChatRoomService chatRoomService;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("존재하지 않는 유저와 채팅방을 생성할 수 없다.")
    public void can_not_create_chat_room_if_partner_is_not_exist() {
        var request = new ChatRoomCreateRequest("test_chat_room", 1L);
        Member member = Member.of(2L, "test_social_id", "test_nickname", "test_profile_image");

        when(memberRepository.existsById(any()))
                .thenReturn(false);

        Assertions.assertThatThrownBy(() -> chatRoomService.createChatRoom(request, member))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(MEMBER_NOT_FOUND.getMessage());
    }
}