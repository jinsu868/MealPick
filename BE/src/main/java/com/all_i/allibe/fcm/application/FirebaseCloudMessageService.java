package com.all_i.allibe.fcm.application;

import com.all_i.allibe.fcm.dto.FcmMessage;
import com.all_i.allibe.member.application.MemberService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirebaseCloudMessageService {

    @Value("${fcm.token.uri}")
    private String API_URL;
    private final String MEDIA_TYPE = "application/json; charset=utf-8";
    private final String FIREBASE_CONFIG_PATH = "firebase/mealpic-77c1d-firebase-adminsdk-fbsvc-6bd026f3d8.json";
    private final ObjectMapper objectMapper;
    private final MemberService memberService;

    public void sendMessageToMembers(
            List<Long> memberIds,
            String title,
            String body
    ) {
        List<String> tokens = memberService.getFcmTokensByMemberIds(memberIds);

        for (String token : tokens) {
            sendMessageTo(
                    token,
                    title,
                    body
            );
        }
    }

    @Async("fcmExecutor")
    public void sendMessageTo(
            String targetToken,
            String title,
            String body
    ) {
        try {
            String message = makeMessage(
                    targetToken,
                    title,
                    body
            );
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message,
                    MediaType.get(MEDIA_TYPE));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + getAccessToken()
                    )
                    .addHeader(
                            HttpHeaders.CONTENT_TYPE,
                            "application/json; UTF-8"
                    )
                    .build();

            Response response = client.newCall(request).execute();
            log.info(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    private String makeMessage(
            String targetToken,
            String title,
            String body
    ) throws JsonParseException, JsonProcessingException, JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        ).build()). validateOnly(false).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = FIREBASE_CONFIG_PATH;

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
