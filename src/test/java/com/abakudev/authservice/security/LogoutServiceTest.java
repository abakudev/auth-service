package com.abakudev.authservice.security;

import com.abakudev.authservice.token.Token;
import com.abakudev.authservice.token.TokenRepository;
import com.abakudev.authservice.token.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LogoutServiceTest {

    private TokenRepository tokenRepository;
    private LogoutService logoutService;
    private String mockJwtToken;
    private Token mockToken;

    @BeforeEach
    void setUp() {
        this.tokenRepository = mock(TokenRepository.class);
        this.logoutService = new LogoutService(this.tokenRepository);
        this.mockJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9"
                + "lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        this.mockToken = Token.builder()
                .id(1)
                .tokenValue(this.mockJwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
    }

    @Test
    void testLogoutOk() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + this.mockJwtToken);
        when(this.tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.ofNullable(this.mockToken));
        when(this.tokenRepository.save(any())).thenReturn(this.mockToken);
        this.logoutService.logout(request, null, null);
        verify(this.tokenRepository, times(1)).findByTokenValue(anyString());
        verify(this.tokenRepository, times(1)).save(any());
    }
}
