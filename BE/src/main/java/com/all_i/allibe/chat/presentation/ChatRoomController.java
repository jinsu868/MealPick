package com.all_i.allibe.chat.presentation;

import com.all_i.allibe.auth.presentation.annotation.AuthMember;
import com.all_i.allibe.chat.application.ChatRoomService;
import com.all_i.allibe.chat.dto.request.ChatRoomCreateRequest;
import com.all_i.allibe.chat.dto.response.ChatRoomCreateResponse;
import com.all_i.allibe.chat.dto.response.ChatRoomResponse;
import com.all_i.allibe.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat-rooms")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoomCreateResponse> createChatRoom(
            @RequestBody ChatRoomCreateRequest chatRoomCreateRequest,
            @AuthMember Member member
    ) {
        var createResponse = chatRoomService.createChatRoom(
                chatRoomCreateRequest,
                member
        );

        return ResponseEntity.ok(createResponse);
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> findAll(@AuthMember Member member) {
        return ResponseEntity.ok(chatRoomService.findAll(member.getId()));
    }
}
