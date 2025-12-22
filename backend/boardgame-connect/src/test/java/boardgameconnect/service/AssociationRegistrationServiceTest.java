package boardgameconnect.service;

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

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.authorization.AssociationDetails;
import boardgameconnect.dto.authorization.RegistrationRequest;
import boardgameconnect.model.Association;
import boardgameconnect.model.Email;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@ExtendWith(MockitoExtension.class)
class AssociationRegistrationServiceTest {

    @Mock
    private UserAccountRepository accountRepo;
    @Mock
    private AssociationRepository associationRepo;
    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private AssociationRegistrationService registrationService;

    @Test
    void registerShouldSaveAccountAndAssociationWhenEmailIsNotRegistered() {
	Email email = new Email("info@associazione.it");
	String rawPassword = "password123";
	String name = "associazione";
	String encodedPassword = "encoded_password";
	var details = new AssociationDetails("test_taxcode", "test_address");
	var association = new Association(new UserAccount(email, encodedPassword, name, UserRole.ASSOCIATION),
		"test_taxcode", "test_address");
	RegistrationRequest<AssociationDetails> request = new RegistrationRequest<>(email, rawPassword, name, details);

	when(accountRepo.findByEmail(email)).thenReturn(Optional.empty());
	when(encoder.encode(rawPassword)).thenReturn(encodedPassword);
	when(associationRepo.save(association)).thenReturn(association);

	registrationService.register(request);
	InOrder inOrder = inOrder(accountRepo, encoder, associationRepo);
	inOrder.verify(accountRepo).findByEmail(email);
	inOrder.verify(encoder).encode(rawPassword);
	inOrder.verify(associationRepo).save(association);
	verifyNoMoreInteractions(accountRepo, encoder, associationRepo);
    }

    @Test
    void registerShouldThrowExceptionWhenEmailAlreadyExists() {
	Email existingEmail = new Email("existing.email@test.it");
	String rawPassword = "password123";
	String name = "associazione";
	var userAccount = new UserAccount(existingEmail, rawPassword, name, UserRole.ASSOCIATION);

	var details = new AssociationDetails("test_taxcode", "test_address");
	RegistrationRequest<AssociationDetails> request = new RegistrationRequest<>(existingEmail, rawPassword, name,
		details);

	when(accountRepo.findByEmail(existingEmail)).thenReturn(Optional.of(userAccount));

	assertThatThrownBy(() -> registrationService.register(request)).isInstanceOf(RuntimeException.class)
		.hasMessage("Email already registered");

	verifyNoMoreInteractions(accountRepo, encoder, associationRepo);
    }
}