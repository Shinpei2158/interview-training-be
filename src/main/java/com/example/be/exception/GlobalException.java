package com.example.be.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode code;

    public GlobalException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public GlobalException(String code, String message) {
        this(ErrorCode.valueOf(code), message);
    }

    public static GlobalException notFound(String message) {
        return new GlobalException(ErrorCode.NOT_FOUND, message);
    }

    public static GlobalException badRequest(String message) {
        return new GlobalException(ErrorCode.BAD_REQUEST, message);
    }

    public static GlobalException unauthorized(String message) {
        return new GlobalException(ErrorCode.UNAUTHORIZED, message);
    }
}
