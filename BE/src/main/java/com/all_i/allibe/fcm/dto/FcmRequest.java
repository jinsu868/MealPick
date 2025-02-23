package com.all_i.allibe.fcm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FcmRequest {
    @NotBlank(message = "FCM 토큰은 필수입니다.")
    private String targetToken;
    @NotBlank(message = "제목을 넣어주세요")
    private String title;
    @NotBlank(message = "내용은 필수 입니다.")
    private String body;
}
