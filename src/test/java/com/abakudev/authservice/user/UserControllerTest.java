package com.abakudev.authservice.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Principal;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {


    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void testChangePasswordOk() throws Exception {

        Principal principal = mock(Principal.class);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("currentPassword",
                "newPassword", "newPassword");
        when(principal.getName()).thenReturn("user");
        doNothing().when(this.userService).changePassword(any(), any());
        this.userController.changePassword(changePasswordRequest, principal);
        verify(this.userService, times(1)).changePassword(any(), any());
    }
}
