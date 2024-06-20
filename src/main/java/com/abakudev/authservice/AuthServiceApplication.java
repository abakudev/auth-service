package com.abakudev.authservice;

import com.abakudev.authservice.user.Role;
import com.abakudev.authservice.auth.AuthenticationService;
import com.abakudev.authservice.auth.request.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class AuthServiceApplication {

    private static final String PASSWORD = "password";

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(AuthenticationService service) {
        return args -> {
            var admin = RegisterRequest.builder()
                    .firstname("Admin")
                    .lastname("Admin")
                    .email("admin@mail.com")
                    .password(PASSWORD)
                    .role(Role.ADMIN)
                    .build();
            log.info("Admin token: {}", service.register(admin).accessToken());

            var manager = RegisterRequest.builder()
                    .firstname("Manager")
                    .lastname("Manager")
                    .email("manager@mail.com")
                    .password(PASSWORD)
                    .role(Role.MANAGER)
                    .build();
            log.info("Manager token: {}", service.register(manager).accessToken());

            var user = RegisterRequest.builder()
                    .firstname("User")
                    .lastname("User")
                    .email("user@mail.com")
                    .password(PASSWORD)
                    .role(Role.USER)
                    .build();
            log.info("User token: {}", service.register(user).accessToken());

        };
    }
}
