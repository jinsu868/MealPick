package com.all_i.allibe.common.exception;

import lombok.Getter;

@Getter
public class SocialLoginException extends RuntimeException {
    private final int code;
    private final String message;

    public SocialLoginException(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}
