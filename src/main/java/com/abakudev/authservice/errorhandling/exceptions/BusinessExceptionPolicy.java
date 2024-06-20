package com.abakudev.authservice.errorhandling.exceptions;

import org.springframework.http.HttpStatus;

public interface BusinessExceptionPolicy extends ExceptionPolicy {

    HttpStatus getHttpStatus();
}
