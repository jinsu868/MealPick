package com.all_i.allibe.common.exception;

public class NotFoundException extends BadRequestException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
