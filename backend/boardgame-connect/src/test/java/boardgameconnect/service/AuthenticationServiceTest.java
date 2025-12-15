package boardgameconnect.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import boardgameconnect.dao.UserRepository;
import boardgameconnect.dto.LoginRequest;
import boardgameconnect.model.Email;
import boardgameconnect.model.Player;
import boardgameconnect.model.User;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginWhenCredentialsAreInvalidTest() {

        User user = new Player(
                "u_123",
                new Email("mario.rossi@example.com"),
                "password",
                "Mario Rossi"
        );

        when(userRepository.findByEmail_Email("mario.rossi@example.com"))
                .thenReturn(user);

        LoginRequest request =
                new LoginRequest("mario.rossi@example.com", "wrong");


        assertThrows(RuntimeException.class,
                () -> authenticationService.login(request));
    }

}