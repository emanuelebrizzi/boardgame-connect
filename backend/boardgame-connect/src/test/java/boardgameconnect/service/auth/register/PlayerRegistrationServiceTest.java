package boardgameconnect.service.auth.register;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import boardgameconnect.dao.PlayerRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.auth.register.RegistrationRequest;
import boardgameconnect.exception.EmailAlreadyInUseException;
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
	@InjectMocks
	private PlayerRegistrationService registerService;

	@Test
	void registerShouldSaveAccountAndAssociationWhenEmailIsNotRegistered() {
		Email email = new Email("mariorosi@dominio.it");
		String rawPassword = "password123";
		String encodedPassword = "encoded_password";
		String name = "Mario Rossi";
		Player player = new Player(new UserAccount(email, encodedPassword, name, UserRole.PLAYER));
		RegistrationRequest<Void> request = new RegistrationRequest<>(email, rawPassword, name, null);

		when(accountRepo.findByEmail(email)).thenReturn(Optional.empty());
		when(encoder.encode(rawPassword)).thenReturn(encodedPassword);
		when(playerRepo.save(player)).thenReturn(player);

		registerService.register(request);
		InOrder inOrder = inOrder(accountRepo, encoder, playerRepo);
		inOrder.verify(accountRepo).findByEmail(email);
		inOrder.verify(encoder).encode(rawPassword);
		inOrder.verify(playerRepo).save(player);
		verifyNoMoreInteractions(accountRepo, encoder, playerRepo);
	}

	@Test
	void registerShouldThrowExceptionWhenEmailAlreadyExists() {
		Email existingEmail = new Email("existing.email@test.it");
		String rawPassword = "password123";
		String name = "Mario Rossi";
		var userAccount = new UserAccount(existingEmail, rawPassword, name, UserRole.PLAYER);

		RegistrationRequest<Void> request = new RegistrationRequest<>(existingEmail, rawPassword, name, null);

		when(accountRepo.findByEmail(existingEmail)).thenReturn(Optional.of(userAccount));

		assertThatThrownBy(() -> registerService.register(request)).isInstanceOf(EmailAlreadyInUseException.class)
				.hasMessage("Email already registered");

		verifyNoMoreInteractions(accountRepo, encoder, playerRepo);
	}
}