package com.abakudev.authservice.security;

import com.abakudev.authservice.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ")) {
            return;
        }
        final int beginIndex = 7;
        jwt = authHeader.substring(beginIndex);
        var storedToken = this.tokenRepository.findByTokenValue(jwt)
                .orElse(null);
        if (Objects.nonNull(storedToken)) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            this.tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
    }
}
