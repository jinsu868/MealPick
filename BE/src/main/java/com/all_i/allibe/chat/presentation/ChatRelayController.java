package com.all_i.allibe.chat.presentation;

import com.all_i.allibe.chat.application.ChatRelayService;
import com.all_i.allibe.chat.dto.response.ChatRelayRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatRelayController {
    private final ChatRelayService chatRelayService;

    @PostMapping("/topic/rooms/{roomId}")
    public void relayMessage(
            @PathVariable("roomId") Long roomId,
            @RequestBody List<ChatRelayRequest> chatRelayRequests
    ) {
        //TODO: 알림 기능 추가
        log.info("{}", chatRelayRequests.size());
        log.info("{}", chatRelayRequests);

        chatRelayService.send(chatRelayRequests, roomId);
    }
}
