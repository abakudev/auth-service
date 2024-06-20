package com.abakudev.authservice.errorhandling.response;

public record InvalidParameter(String parameter,
                               String message) {
}
