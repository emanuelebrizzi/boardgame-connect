package boardgameconnect.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.Key;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import boardgameconnect.model.Email;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

	@InjectMocks
	private JwtService jwtService;

	private static final String TEST_SECRET = Base64.getEncoder()
			.encodeToString("QuestaChiaveSegretaPerIUnitTestDeveEssereMoltoLungaECompleta1234567890".getBytes());
	private static final Email email = new Email("user@domain.com");
	private Key testKey;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
		jwtService.init();
		this.testKey = Keys.hmacShaKeyFor(TEST_SECRET.getBytes());
	}

	@Test
	void testGenerateTokenWhenEmailIsValidShouldReturnSignedJwt() {
		String token = jwtService.generateToken(email);

		assertNotNull(token);
		assertFalse(token.isEmpty());
		assertTrue(token.contains("."));
	}
}