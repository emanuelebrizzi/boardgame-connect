package boardgameconnect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import boardgameconnect.dto.RegistrationRequest;
import boardgameconnect.model.Email;
import boardgameconnect.model.Player;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@ExtendWith(MockitoExtension.class)
class PlayerRegistrationServiceTest {

    @Mock
    private UserAccountRepository accountRepo;
    @Mock
    private PlayerRepository playerRepo;
    @Mock
    private PasswordEncoder encoder;

    private PlayerRegistrationService registrationService;

    @BeforeEach
    void setUp() {
	registrationService = new PlayerRegistrationService(accountRepo, playerRepo, encoder);
    }

    @Test
    void register_ShouldSaveAccountAndAssociation_WhenEmailIsNotRegistered() {

	Email email = new Email("mariorosi@dominio.it");
	String rawPassword = "password123";
	String encodedPassword = "encoded_password";

	PlayerDto details = new PlayerDto(null, email.getEmail(), "Mario Rossi", UserRole.PLAYER);
	RegistrationRequest<PlayerDto> request = new RegistrationRequest<PlayerDto>(email, rawPassword, details);

	when(accountRepo.findByEmail(email)).thenReturn(Optional.empty());
	when(encoder.encode(rawPassword)).thenReturn(encodedPassword);

	registrationService.register(request);

	ArgumentCaptor<UserAccount> accountCaptor = ArgumentCaptor.forClass(UserAccount.class);
	verify(accountRepo).save(accountCaptor.capture());
	UserAccount savedAccount = accountCaptor.getValue();

	assertEquals(email, savedAccount.getEmail());
	assertEquals(encodedPassword, savedAccount.getPassword());

	ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
	verify(playerRepo).save(playerCaptor.capture());
	Player savedPlayer = playerCaptor.getValue();

	assertEquals(savedAccount, savedPlayer.getAccount());
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {

	Email existingEmail = new Email("existing.email@test.it");
	String rawPassword = "password123";

	PlayerDto details = new PlayerDto(null, existingEmail.getEmail(), "Mario Rossi", UserRole.PLAYER);
	RegistrationRequest<PlayerDto> request = new RegistrationRequest<PlayerDto>(existingEmail, rawPassword,
		details);

	UserAccount mockAccount = mock(UserAccount.class);
	when(accountRepo.findByEmail(existingEmail)).thenReturn(Optional.of(mockAccount));

	RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	    registrationService.register(request);
	});

	assertEquals("Email already registered", exception.getMessage());

	verify(accountRepo, never()).save(any());
	verify(playerRepo, never()).save(any());
    }
}