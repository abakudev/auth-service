package com.abakudev.authservice.auth;

import com.abakudev.authservice.auth.response.AuthenticationResponse;
import com.abakudev.authservice.security.JwtService;
import com.abakudev.authservice.token.Token;
import com.abakudev.authservice.token.TokenRepository;
import com.abakudev.authservice.token.TokenType;
import com.abakudev.authservice.user.Role;
import com.abakudev.authservice.user.User;
import com.abakudev.authservice.user.UserRepository;
import com.abakudev.authservice.auth.request.LoginRequest;
import com.abakudev.authservice.auth.request.RefreshTokenRequest;
import com.abakudev.authservice.auth.request.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class AuthenticationServiceTest {

    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private AuthenticationService authenticationService;

    private User mockUser;
    private Token mockToken;
    private String mockJwtToken;
    private RegisterRequest mockRegisterRequest;
    private LoginRequest mockLoginRequest;
    private AuthenticationResponse mockAuthenticationResponse;

    @BeforeEach
    void setUp() {
        this.userRepository = mock(UserRepository.class);
        this.tokenRepository = mock(TokenRepository.class);
        this.passwordEncoder = mock(PasswordEncoder.class);
        this.jwtService = mock(JwtService.class);
        this.authenticationManager = mock(AuthenticationManager.class);
        this.authenticationService = new AuthenticationService(userRepository, tokenRepository, passwordEncoder,
                jwtService, authenticationManager);

        this.mockJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9"
                + "lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        this.mockToken = Token.builder()
                .id(1)
                .tokenValue(this.mockJwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        this.mockUser = User.builder()
                .firstname("firstname")
                .lastname("lastname")
                .email("user@mail.com")
                .password("password")
                .role(Role.USER)
                .tokens(List.of(this.mockToken))
                .build();

        this.mockRegisterRequest = RegisterRequest.builder()
                .firstname("firstname")
                .lastname("lastname")
                .email("user@mail.com")
                .password("password")
                .role(Role.USER)
                .build();
        this.mockLoginRequest = new LoginRequest("user@mail.com", "password");
        this.mockAuthenticationResponse = new AuthenticationResponse(this.mockJwtToken, this.mockJwtToken);
    }

    @Test
    void givenRegisterRequestWhenRegisterThenRegisterUserSuccessfully() {
        when(userRepository.save(any())).thenReturn(this.mockUser);
        when(tokenRepository.save(any())).thenReturn(this.mockToken);
        when(passwordEncoder.encode(any())).thenReturn(this.mockJwtToken);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(this.mockJwtToken);
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn(this.mockJwtToken);
        var actualResponse = authenticationService.register(this.mockRegisterRequest);
        assertEquals(this.mockAuthenticationResponse, actualResponse);
    }

    @Test
    void givenLoginRequestWhenAuthenticateThenAuthenticateUserSuccessfully() {
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(this.mockUser));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(this.mockJwtToken);
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn(this.mockJwtToken);
        when(tokenRepository.findAllValidTokenByUser(anyInt())).thenReturn(List.of(this.mockToken));
        when(tokenRepository.saveAll(any())).thenReturn(List.of(this.mockToken));
        when(tokenRepository.save(any())).thenReturn(this.mockToken);
        AuthenticationResponse actualResponse = authenticationService.authenticate(this.mockLoginRequest);
        assertEquals(this.mockAuthenticationResponse, actualResponse);
    }

    @Test
    void givenRefreshTokenRequestWhenRefreshTokenThenRefreshTokenSuccessfully() throws IOException {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(this.mockUser));
        when(jwtService.extractUsername(anyString())).thenReturn("user@mail.com");
        when(jwtService.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(true);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(this.mockJwtToken);
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn(this.mockJwtToken);
        when(tokenRepository.save(any())).thenReturn(this.mockToken);
        when(tokenRepository.saveAll(any())).thenReturn(List.of(this.mockToken));
        var actualResponse = this.authenticationService
                .refreshToken(new RefreshTokenRequest(this.mockJwtToken));
        assertEquals(this.mockAuthenticationResponse, actualResponse);
    }

}
