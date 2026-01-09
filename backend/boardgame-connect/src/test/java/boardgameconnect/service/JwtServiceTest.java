package boardgameconnect.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import boardgameconnect.model.Email;
import boardgameconnect.model.UserAccount;
import boardgameconnect.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtServiceTest {
	private static final String USER_EMAIL_STRING = "user@domain.com";
	private static final String TEST_SECRET = "ThisIsAVeryLongSecretKeyUsedForUnitTestingPurposesOnly1234567890";
	private static final long TEST_EXPIRATION = 3600000;

	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		jwtService = new JwtService();
		jwtService.setSecret(TEST_SECRET);
		jwtService.setJwtExpiration(TEST_EXPIRATION);
	}

	@Test
	void generateTokenShouldContainCorrectClaimsAndRole() {
		var userAccount = new UserAccount(new Email(USER_EMAIL_STRING), "encoded_password", "username",
				UserRole.PLAYER);

		String token = jwtService.generateToken(userAccount);
		Claims claims = Jwts.parserBuilder().setSigningKey(jwtService.getSigningKey()).build().parseClaimsJws(token)
				.getBody();

		assertThat(userAccount.getEmail().toString()).isEqualTo(claims.getSubject());
		assertThat(userAccount.getUserRole().toString()).isEqualTo(claims.get("role"));
	}

}