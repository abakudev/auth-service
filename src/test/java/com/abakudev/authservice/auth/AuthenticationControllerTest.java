package com.abakudev.authservice.auth;

import com.abakudev.authservice.auth.request.LoginRequest;
import com.abakudev.authservice.auth.request.RegisterRequest;
import com.abakudev.authservice.auth.response.AuthenticationResponse;
import com.abakudev.authservice.user.Role;
import com.abakudev.authservice.auth.request.RefreshTokenRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationControllerTest {

    private AuthenticationService authenticationService;
    private AuthenticationController authenticationController;

    private AuthenticationResponse authenticationResponse;
    private String email = "email@mail.com";
    private String password = "password";

    @BeforeEach
    void setUp() {
        this.authenticationService = mock(AuthenticationService.class);
        this.authenticationController = new AuthenticationController(authenticationService);

        this.authenticationResponse = new AuthenticationResponse("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
                + "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJ"
                + "f36POk6yJV_adQssw5c", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIi"
                + "wibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
    }

    @Test
    void givenRegisterRequestWhenRegisterThenRegisterUserSuccessfully() {
        var registerRequest = new RegisterRequest("firstname", "lastname", email, password, Role.USER);
        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(this.authenticationResponse);
        var result = authenticationController.register(registerRequest);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void givenLoginRequestWhenAuthenticateThenAuthenticateUserSuccessfully() {
        when(authenticationService.authenticate(any(LoginRequest.class))).thenReturn(this.authenticationResponse);
        var result = authenticationController.authenticate(new LoginRequest(email, password));
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void givenRefreshTokenRequestWhenRefreshTokenThenRefreshTokenSuccessfully() throws IOException {
        var token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
                + "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJ"
                + "f36POk6yJV_adQssw5c";
        when(authenticationService.refreshToken(any())).thenReturn(this.authenticationResponse);
        var result = authenticationController.refreshToken(new RefreshTokenRequest(token));
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
