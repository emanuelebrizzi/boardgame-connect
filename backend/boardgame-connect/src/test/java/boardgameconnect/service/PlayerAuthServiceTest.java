package boardgameconnect.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.LoginRequest;
import boardgameconnect.dto.LoginResponse;
import boardgameconnect.dto.PlayerDto;
import boardgameconnect.mapper.UserMapper;
import boardgameconnect.model.Email;
import boardgameconnect.model.Player;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@ExtendWith(MockitoExtension.class)
class PlayerAuthServiceTest {

    @Mock
    private UserAccountRepository accountRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private JwtService jwtService;

    private PasswordEncoder passwordEncoder;
    private PlayerAuthService playerAuthService;

    @BeforeEach
    void setUp() {
	// Simple encoder for fast testing
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

	playerAuthService = new PlayerAuthService(accountRepository, playerRepository, passwordEncoder, userMapper,
		jwtService);
    }

    @Test
    void loginShouldThrowWhenAccountNotFound() {
	Email email = new Email("unknown@example.com");
	LoginRequest request = new LoginRequest(email, "password");

	when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());

	assertThatThrownBy(() -> playerAuthService.login(request)).isInstanceOf(RuntimeException.class)
		.hasMessage("Invalid credentials");

	verifyNoMoreInteractions(playerRepository, userMapper, jwtService);
    }

    @Test
    void loginShouldThrowWhenPasswordIsInvalid() {
	Email email = new Email("mario@example.com");
	String rawPassword = "password";
	String encodedPassword = passwordEncoder.encode(rawPassword);
	UserAccount account = new UserAccount(email, encodedPassword, "example", UserRole.PLAYER);

	when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

	LoginRequest request = new LoginRequest(email, "wrong_password");

	assertThatThrownBy(() -> playerAuthService.login(request)).isInstanceOf(RuntimeException.class)
		.hasMessage("Invalid credentials");

	verifyNoMoreInteractions(playerRepository, userMapper, jwtService);
    }

    @Test
    void loginShouldReturnResponseWhenCredentialsAreValid() {
	Email email = new Email("mario@example.com");
	String rawPassword = "password";
	String encodedPassword = passwordEncoder.encode(rawPassword);
	UserAccount account = new UserAccount(email, encodedPassword, "example", UserRole.PLAYER);
	Player player = new Player(account);
	String mockToken = "mocked-jwt-token";
	PlayerDto expectedDto = new PlayerDto("id_123", "mario@example.com", "Mario", UserRole.PLAYER);

	when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));
	when(playerRepository.findByAccount(account)).thenReturn(Optional.of(player));
	when(jwtService.generateToken(account)).thenReturn(mockToken);
	when(userMapper.toDto(player)).thenReturn(expectedDto);

	LoginRequest request = new LoginRequest(email, rawPassword);
	LoginResponse<PlayerDto> response = playerAuthService.login(request);

	assertThat(response.accessToken()).isEqualTo(mockToken);
	assertThat(response.profile()).isEqualTo(expectedDto);

	InOrder inOrder = inOrder(accountRepository, playerRepository, userMapper, jwtService);
	inOrder.verify(accountRepository).findByEmail(email);
	inOrder.verify(playerRepository).findByAccount(account);
	inOrder.verify(jwtService).generateToken(account);
	inOrder.verify(userMapper).toDto(player);
    }
}