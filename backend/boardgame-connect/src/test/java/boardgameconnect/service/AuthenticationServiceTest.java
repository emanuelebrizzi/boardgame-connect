package boardgameconnect.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import boardgameconnect.dto.LoginRequest;

class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService();
    }

    @Test
    void login_fail_with_wrong_password() {
        LoginRequest request =
                new LoginRequest("mario.rossi@example.com", "wrong");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authenticationService.login(request)
        );

        assertEquals("Credenziali non valide", exception.getMessage());
    }
}
