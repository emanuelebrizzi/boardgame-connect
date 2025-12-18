package boardgameconnect.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.PlayerDto;
import boardgameconnect.dto.PlayerRegistrationDetails;
import boardgameconnect.dto.RegistrationRequest;
import boardgameconnect.mapper.UserMapper;
import boardgameconnect.model.Email;
import boardgameconnect.model.Player;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@ExtendWith(MockitoExtension.class)
class PlayerRegistrationServiceTest {

    @Mock
    private UserAccountRepository accountRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private PlayerRegistrationService playerRegistrationService;

    @BeforeEach
    void setUp() {
	playerRegistrationService = new PlayerRegistrationService(accountRepository, playerRepository, passwordEncoder,
		userMapper);
    }

    @Test
    void registerWhenEmailAlreadyExistsShouldThrowException() {
	Email existingEmail = new Email("mario@example.com");
	PlayerRegistrationDetails details = new PlayerRegistrationDetails();
	RegistrationRequest<PlayerRegistrationDetails> request = new RegistrationRequest<>(existingEmail, "password",
		"Mario Rossi", details);

	when(accountRepository.findByEmail(existingEmail)).thenReturn(Optional.of(mock(UserAccount.class)));
	assertThatThrownBy(() -> playerRegistrationService.register(request)).isInstanceOf(RuntimeException.class)
		.hasMessage("Email already registered");
	verify(playerRepository, never()).save(any());
    }

    @Test
    void registerShouldSuccessfullySavePlayerWithEncryptedPassword() {
	Email email = new Email("newplayer@domain.com");
	PlayerRegistrationDetails details = new PlayerRegistrationDetails();
	RegistrationRequest<PlayerRegistrationDetails> request = new RegistrationRequest<>(email, "password",
		"Mario Rossi", details);

	when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());
	when(passwordEncoder.encode(anyString())).thenReturn("encrypted_pass");

	Player savedPlayer = mock(Player.class);
	when(playerRepository.save(any(Player.class))).thenReturn(savedPlayer);
	when(userMapper.toDto(savedPlayer)).thenReturn(mock(PlayerDto.class));

	playerRegistrationService.register(request);

	ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
	verify(playerRepository).save(playerCaptor.capture());

	Player captured = playerCaptor.getValue();
	assertThat(captured.getAccount().getUserRole()).isEqualTo(UserRole.PLAYER);
    }
}