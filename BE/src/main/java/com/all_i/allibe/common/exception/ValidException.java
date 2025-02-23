package com.all_i.allibe.common.exception;

public class ValidException extends BadRequestException {

    public ValidException(String message) {
        super(ErrorCode.VALIDATION_FAIL);
    }
}
