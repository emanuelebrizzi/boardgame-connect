package boardgameconnect.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.LoginRequest;
import boardgameconnect.dto.LoginResponse;
import boardgameconnect.model.Email;
import boardgameconnect.model.Player;
import boardgameconnect.model.UserAccount;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserAccountRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
	// Fake implementation for test performance: using the a real encoder would slow
	// down the unit test.
	passwordEncoder = new PasswordEncoder() {
	    @Override
	    public String encode(CharSequence rawPassword) {
		return "ENC_" + rawPassword;
	    }

	    @Override
	    public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return encodedPassword.equals("ENC_" + rawPassword);
	    }
	};

	authenticationService = new AuthenticationService(userRepository, passwordEncoder);
    }

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
	String rawPassword = "password";
	String dbPassword = passwordEncoder.encode(rawPassword);
	UserAccount user = new Player(email, dbPassword, "Mario Rossi");

	when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

	LoginRequest request = new LoginRequest(email, rawPassword);
	LoginResponse response = authenticationService.login(request);

	assertThat(response.getAccessToken()).isEqualTo("valid token");
	assertThat(response.getUser()).isEqualTo(user);
    }

    @Test
    void testLoginShouldThrowExceptionWhenPasswordIsWrong() {
	var email = new Email("mario.rossi@example.com");
	String rawPassword = "password";
	String dbPassword = passwordEncoder.encode(rawPassword);
	UserAccount user = new Player(email, dbPassword, "Mario Rossi");

	when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

	LoginRequest request = new LoginRequest(email, "wrong_password");

	assertThatThrownBy(() -> authenticationService.login(request)).isInstanceOf(RuntimeException.class)
		.hasMessage("Invalid credentials");
    }

}