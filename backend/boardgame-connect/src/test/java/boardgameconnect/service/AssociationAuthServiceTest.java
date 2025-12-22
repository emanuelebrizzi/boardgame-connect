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

import boardgameconnect.dao.AssociationRepository;
import boardgameconnect.dao.UserAccountRepository;
import boardgameconnect.dto.AssociationDto;
import boardgameconnect.dto.authorization.LoginRequest;
import boardgameconnect.dto.authorization.LoginResponse;
import boardgameconnect.mapper.UserMapper;
import boardgameconnect.model.Association;
import boardgameconnect.model.Email;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;

@ExtendWith(MockitoExtension.class)
class AssociationAuthServiceTest {

    @Mock
    private UserAccountRepository accountRepository;
    @Mock
    private AssociationRepository associationRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private JwtService jwtService;

    private PasswordEncoder passwordEncoder;
    private AssociationAuthService associationAuthService;

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

	associationAuthService = new AssociationAuthService(accountRepository, associationRepository, passwordEncoder,
		userMapper, jwtService);
    }

    @Test
    void loginShouldThrowWhenAccountNotFound() {
	Email email = new Email("unknown@example.com");
	LoginRequest request = new LoginRequest(email, "password");

	when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());

	assertThatThrownBy(() -> associationAuthService.login(request)).isInstanceOf(RuntimeException.class)
		.hasMessage("Invalid credentials");

	verifyNoMoreInteractions(associationRepository, userMapper, jwtService);
    }

    @Test
    void loginShouldThrowWhenPasswordIsInvalid() {
	Email email = new Email("association@example.com");
	String rawPassword = "password";
	String encodedPassword = passwordEncoder.encode(rawPassword);
	UserAccount account = new UserAccount(email, encodedPassword, "example", UserRole.ASSOCIATION);

	when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

	LoginRequest request = new LoginRequest(email, "wrong_password");

	assertThatThrownBy(() -> associationAuthService.login(request)).isInstanceOf(RuntimeException.class)
		.hasMessage("Invalid credentials");

	verifyNoMoreInteractions(associationRepository, userMapper);
    }

    @Test
    void loginShouldReturnResponseWhenCredentialsAreValid() {
	Email email = new Email("association@example.com");
	String rawPassword = "password";
	String encodedPassword = passwordEncoder.encode(rawPassword);
	UserAccount account = new UserAccount(email, encodedPassword, "example", UserRole.ASSOCIATION);
	Association association = new Association(account, "test_taxcode", "test_address");
	String mockToken = "mocked-jwt-token";
	AssociationDto expectedDto = new AssociationDto("assoc_id", "association@example.com", "Assoc Name",
		"test_taxcode", "Via Roma 1", UserRole.ASSOCIATION);

	when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));
	when(associationRepository.findByAccount(account)).thenReturn(Optional.of(association));
	when(jwtService.generateToken(account)).thenReturn(mockToken);
	when(userMapper.toDto(association)).thenReturn(expectedDto);

	LoginRequest request = new LoginRequest(email, rawPassword);
	LoginResponse<AssociationDto> response = associationAuthService.login(request);

	assertThat(response.accessToken()).isEqualTo(mockToken);
	assertThat(response.profile()).isEqualTo(expectedDto);

	InOrder inOrder = inOrder(accountRepository, associationRepository, userMapper, jwtService);
	inOrder.verify(accountRepository).findByEmail(email);
	inOrder.verify(associationRepository).findByAccount(account);
	inOrder.verify(jwtService).generateToken(account);
	inOrder.verify(userMapper).toDto(association);
    }
}