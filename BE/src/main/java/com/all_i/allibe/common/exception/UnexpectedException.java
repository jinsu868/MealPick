package com.all_i.allibe.common.exception;

public class UnexpectedException extends BadRequestException {

    public UnexpectedException(String message) {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }

}
