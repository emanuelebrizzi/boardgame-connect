package boardgameconnect.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
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
import boardgameconnect.dto.AssociationRegistrationDetails;
import boardgameconnect.dto.RegistrationRequest;
import boardgameconnect.mapper.UserMapper;
import boardgameconnect.model.Association;
import boardgameconnect.model.Email;
import boardgameconnect.model.UserRole;

@ExtendWith(MockitoExtension.class)
class AssociationRegistrationServiceTest {

    @Mock
    private UserAccountRepository accountRepository;
    @Mock
    private AssociationRepository associationRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AssociationRegistrationService associationRegistrationService;

    @BeforeEach
    void setUp() {
	associationRegistrationService = new AssociationRegistrationService(accountRepository, associationRepository,
		passwordEncoder, userMapper);
    }

    @Test
    void registerShouldSaveAssociationWithCorrectDetails() {

	Email email = new Email("assoc@test.com");
	AssociationRegistrationDetails details = new AssociationRegistrationDetails("123456789", "Via Roma 1");
	RegistrationRequest<AssociationRegistrationDetails> request = new RegistrationRequest<>(email, "password",
		"assoc_user", details);

	when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());
	when(passwordEncoder.encode(anyString())).thenReturn("encrypted_pass");

	Association savedAssoc = mock(Association.class);
	when(associationRepository.save(any(Association.class))).thenReturn(savedAssoc);
	when(userMapper.toDto(savedAssoc)).thenReturn(mock(AssociationDto.class));

	associationRegistrationService.register(request);

	ArgumentCaptor<Association> assocCaptor = ArgumentCaptor.forClass(Association.class);
	verify(associationRepository).save(assocCaptor.capture());

	Association captured = assocCaptor.getValue();
	assertThat(captured.getTaxCode()).isEqualTo("123456789");
	assertThat(captured.getAddress()).isEqualTo("Via Roma 1");
	assertThat(captured.getAccount().getUserRole()).isEqualTo(UserRole.ASSOCIATION);
    }
}