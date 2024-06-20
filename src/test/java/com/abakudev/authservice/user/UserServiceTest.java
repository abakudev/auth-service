package com.abakudev.authservice.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private PasswordEncoder passwordEncoder;

    private UserRepository userRepository;

    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        this.passwordEncoder = mock(PasswordEncoder.class);
        this.userRepository = mock(UserRepository.class);
        this.userService = new UserService(passwordEncoder, userRepository);

        this.mockUser = User.builder()
                .firstname("firstname")
                .lastname("lastname")
                .email("user@mail.com")
                .password("newPassword")
                .role(Role.USER)
                .build();
    }

    @Test
    void testChangePasswordOk() {

        when(this.passwordEncoder.encode(any())).thenReturn("encodeResponse");
        when(this.passwordEncoder.matches(any(), anyString())).thenReturn(true);
        when(this.userRepository.save(any())).thenReturn(this.mockUser);
        UsernamePasswordAuthenticationToken connectedUser = new UsernamePasswordAuthenticationToken(this.mockUser, null);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("currentPassword",
                "newPassword", "newPassword");
        this.userService.changePassword(changePasswordRequest, connectedUser);
        verify(this.userRepository, times(1)).save(any());
    }
}
