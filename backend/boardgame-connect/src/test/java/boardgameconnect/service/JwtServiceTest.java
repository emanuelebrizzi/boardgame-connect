package boardgameconnect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.Key;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import boardgameconnect.model.Email;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private static final String TEST_SECRET = "ThisIsAVeryLongSecretKeyUsedForUnitTestingPurposesOnly1234567890";
    private static final long TEST_EXPIRATION = 3600000;

    private UserAccount testAccount;
    private Key testKey;

    @BeforeEach
    void setUp() {
	ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
	ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_EXPIRATION);
	jwtService.init();
	this.testKey = Keys.hmacShaKeyFor(TEST_SECRET.getBytes());
	Email email = new Email("user@domain.com");
	this.testAccount = new UserAccount(email, "encoded_password", "username", UserRole.PLAYER);
    }

    @Test
    void generateTokenWhenEmailIsValidShouldReturnSignedJwt() {
	String token = jwtService.generateToken(testAccount);

	assertNotNull(token);
	assertFalse(token.isEmpty());
	assertTrue(token.contains("."));
    }

    @Test
    void generateTokenShouldContainCorrectClaimsAndRole() {
	String token = jwtService.generateToken(testAccount);

	Claims claims = Jwts.parserBuilder().setSigningKey(testKey).build().parseClaimsJws(token).getBody();

	assertEquals(testAccount.getEmail().toString(), claims.getSubject());
	assertEquals(testAccount.getUserRole().toString(), claims.get("role"));
	assertNotNull(claims.getIssuedAt());
	long diff = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
	assertEquals(TEST_EXPIRATION, diff);
    }
}