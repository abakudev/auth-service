package com.abakudev.authservice.errorhandling.response;

import com.abakudev.authservice.errorhandling.exceptions.BusinessExceptionReason;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;


public record ErrorResponse(String code,
                            String message,
                            Integer status,
                            LocalDateTime timestamp,
                            List<InvalidParameter> invalidParameters) {

    /**
     * Builds an error response based on given parameters.
     *
     * @param code    the error code
     * @param message the error message
     * @param status  the http status attached to the error
     */
    public ErrorResponse(String code, String message, HttpStatus status) {
        this(code, message, status.value(), LocalDateTime.now(), null);
    }

    /**
     * Builds an error response based on given parameters.
     *
     * @param code              the error code
     * @param message           the error message
     * @param status            the http status attached to the error
     * @param invalidParameters the list of invalid parameters
     */
    public ErrorResponse(String code, String message, HttpStatus status, List<InvalidParameter> invalidParameters) {
        this(code, message, status.value(), LocalDateTime.now(), invalidParameters);
    }

    public ErrorResponse(BusinessExceptionReason reason) {
        this(reason.getCode(), reason.getMessage(), reason.getHttpStatus().value(), LocalDateTime.now(), null);
    }

}

