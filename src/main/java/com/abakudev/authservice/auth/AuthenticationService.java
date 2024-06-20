package com.abakudev.authservice.auth;

import com.abakudev.authservice.auth.request.LoginRequest;
import com.abakudev.authservice.auth.request.RegisterRequest;
import com.abakudev.authservice.auth.response.AuthenticationResponse;
import com.abakudev.authservice.errorhandling.exceptions.BusinessException;
import com.abakudev.authservice.errorhandling.exceptions.BusinessExceptionReason;
import com.abakudev.authservice.security.JwtService;
import com.abakudev.authservice.token.Token;
import com.abakudev.authservice.token.TokenRepository;
import com.abakudev.authservice.token.TokenType;
import com.abakudev.authservice.user.User;
import com.abakudev.authservice.user.UserRepository;
import com.abakudev.authservice.auth.request.RefreshTokenRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        this.userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new BusinessException(BusinessExceptionReason.USER_ALREADY_EXISTS);
                });
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(this.passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        var savedUser = this.userRepository.save(user);
        var jwtToken = this.jwtService.generateToken(user);
        var refreshToken = this.jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    public AuthenticationResponse authenticate(LoginRequest request) {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = this.userRepository.findByEmail(request.getEmail())
                .orElseThrow( () -> new BusinessException(BusinessExceptionReason.USER_NOT_FOUND));
        var jwtToken = this.jwtService.generateToken(user);
        var refreshToken = this.jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .tokenValue(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        this.tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = this.tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        this.tokenRepository.saveAll(validUserTokens);
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {

        if (Objects.isNull(request) || Strings.isBlank(request.token())) {
            throw new BusinessException(BusinessExceptionReason.INVALID_TOKEN);
        }
        final String refreshToken = request.token();
        final String userEmail;
        userEmail = this.jwtService.extractUsername(refreshToken);
        if (Strings.isNotBlank(userEmail)) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (this.jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = this.jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                return new AuthenticationResponse(accessToken, refreshToken);
            }
        }
        throw new BusinessException(BusinessExceptionReason.INVALID_TOKEN);
    }
}
