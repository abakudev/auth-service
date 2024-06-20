package com.abakudev.authservice.errorhandling.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BusinessExceptionReason implements BusinessExceptionPolicy {

    ERROR("E0001", "Error General.", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_ALREADY_EXISTS("E0002", "User already exists!", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN("E0003", "Invalid token.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("E0004", "User not found!", HttpStatus.NOT_FOUND),
    ACCESS_DENIED("E0005", "Access denied!", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
