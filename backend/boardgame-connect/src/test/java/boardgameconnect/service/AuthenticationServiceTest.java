package boardgameconnect.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import boardgameconnect.dao.UserRepository;
import boardgameconnect.dto.LoginRequest;
import boardgameconnect.dto.LoginResponse;
import boardgameconnect.model.Email;
import boardgameconnect.model.Player;
import boardgameconnect.model.User;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void testLoginWhenCredentialsAreInvalidTestShouldThrow() {
	var email = new Email("mario.rossi@example.com");

	when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

	LoginRequest request = new LoginRequest(email, "wrong");

	assertThatThrownBy(() -> authenticationService.login(request)).isInstanceOf(RuntimeException.class)
		.hasMessageContaining("Invalid credentials");
    }

    @Test
    void testLoginWhenCredentialsAreValidTestShouldReturnLoginResponse() {
	var email = new Email("mario.rossi@example.com");
	User user = new Player(email, "password", "Mario Rossi");

	when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

	LoginRequest request = new LoginRequest(email, "password");
	LoginResponse response = authenticationService.login(request);

	assertThat(response.getAccessToken()).isEqualTo("valid token");
	assertThat(response.getUser()).isEqualTo(user);
    }

    @Test
    void testLoginShouldThrowExceptionWhenPasswordIsWrong() {
	var email = new Email("mario.rossi@example.com");
	User user = new Player(email, "password", "Mario Rossi");

	when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

	LoginRequest request = new LoginRequest(email, "wrong_password");

	assertThatThrownBy(() -> authenticationService.login(request)).isInstanceOf(RuntimeException.class)
		.hasMessage("Invalid credentials");
    }

}