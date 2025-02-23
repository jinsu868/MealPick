package com.all_i.allibe.fcm.presentation;

import com.all_i.allibe.fcm.application.FirebaseCloudMessageService;
import com.all_i.allibe.fcm.dto.FcmRequest;
import com.all_i.allibe.fcm.dto.FcmRequests;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
@Slf4j
public class FcmController {

    private final FirebaseCloudMessageService firebaseCloudMessageService;

    @PostMapping
    public ResponseEntity<Void> pushMessage(@Valid @RequestBody FcmRequest request) throws IOException {
        firebaseCloudMessageService.sendMessageTo(
                request.getTargetToken(),
                request.getTitle(),
                request.getBody());

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/send")
    public ResponseEntity<Void> pushMessages(@Valid @RequestBody FcmRequests requests) {
        firebaseCloudMessageService.sendMessageToMembers(
                requests.getMemberIds(),
                requests.getTitle(),
                requests.getBody());
        return ResponseEntity.ok().build();
    }
}
