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

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.AssociationDto;
import boardgameconnect.dto.RegistrationRequest;
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

    private AssociationRegistrationService registrationService;

    @BeforeEach
    void setUp() {
	registrationService = new AssociationRegistrationService(accountRepo, associationRepo, encoder);
    }

    @Test
    void register_ShouldSaveAccountAndAssociation_WhenEmailIsNotRegistered() {

	Email email = new Email("info@associazione.it");
	String rawPassword = "password123";
	String encodedPassword = "encoded_password";

	AssociationDto details = new AssociationDto(null, email.getEmail(), "Ludoteca Serale", "123456789",
		"Via Roma 1", UserRole.ASSOCIATION);
	RegistrationRequest<AssociationDto> request = new RegistrationRequest<AssociationDto>(email, rawPassword,
		details);

	when(accountRepo.findByEmail(email)).thenReturn(Optional.empty());
	when(encoder.encode(rawPassword)).thenReturn(encodedPassword);

	registrationService.register(request);

	ArgumentCaptor<UserAccount> accountCaptor = ArgumentCaptor.forClass(UserAccount.class);
	verify(accountRepo).save(accountCaptor.capture());
	UserAccount savedAccount = accountCaptor.getValue();

	assertEquals(email, savedAccount.getEmail());
	assertEquals(encodedPassword, savedAccount.getPassword());

	ArgumentCaptor<Association> associationCaptor = ArgumentCaptor.forClass(Association.class);
	verify(associationRepo).save(associationCaptor.capture());
	Association savedAssociation = associationCaptor.getValue();

	assertEquals("123456789", savedAssociation.getTaxCode());
	assertEquals(savedAccount, savedAssociation.getAccount());
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {

	Email existingEmail = new Email("existing.email@test.it");
	String rawPassword = "password123";

	AssociationDto details = new AssociationDto(null, existingEmail.getEmail(), "Ludoteca Serale", "123456789",
		"Via Roma 1", UserRole.ASSOCIATION);
	RegistrationRequest<AssociationDto> request = new RegistrationRequest<AssociationDto>(existingEmail,
		rawPassword, details);

	UserAccount mockAccount = mock(UserAccount.class);
	when(accountRepo.findByEmail(existingEmail)).thenReturn(Optional.of(mockAccount));

	RuntimeException exception = assertThrows(RuntimeException.class, () -> {
	    registrationService.register(request);
	});

	assertEquals("Email already registered", exception.getMessage());

	verify(accountRepo, never()).save(any());
	verify(associationRepo, never()).save(any());
    }
}