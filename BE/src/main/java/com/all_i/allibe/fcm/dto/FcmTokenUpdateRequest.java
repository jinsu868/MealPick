package com.all_i.allibe.fcm.dto;

import com.all_i.allibe.common.exception.BadRequestException;
import com.all_i.allibe.common.exception.ErrorCode;

public record FcmTokenUpdateRequest(
        String fcmToken
) {
    public void validate() {
        if (fcmToken == null || fcmToken.isBlank()) {
            throw new BadRequestException(ErrorCode.FAILED_TO_VALIDATE_TOKEN);
        }
    }
}
