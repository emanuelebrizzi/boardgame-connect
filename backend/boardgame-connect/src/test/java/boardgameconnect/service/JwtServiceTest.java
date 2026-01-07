package boardgameconnect.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

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
		jwtService.init();
	}

	@Test
	void generateTokenShouldContainCorrectClaimsAndRole() {
		var userAccount = new UserAccount(new Email(USER_EMAIL_STRING), "encoded_password", "username",
				UserRole.PLAYER);

		String token = jwtService.generateToken(userAccount);
		Claims claims = Jwts.parserBuilder().setSigningKey(jwtService.getSecretKey()).build().parseClaimsJws(token)
				.getBody();

		assertThat(userAccount.getEmail().toString()).isEqualTo(claims.getSubject());
		assertThat(userAccount.getUserRole().toString()).isEqualTo(claims.get("role"));
	}

	@Test
	void extractSubjectShouldReturnCorrectSubject() {
		var userAccount = new UserAccount(new Email(USER_EMAIL_STRING), "pass", "user", UserRole.ASSOCIATION);
		String token = jwtService.generateToken(userAccount);
		String extractedUsername = jwtService.extractSubject(token);
		assertThat(extractedUsername).isEqualTo(USER_EMAIL_STRING);
	}

	@Test
	void extractUserRoleShouldReturnCorrectRole() {
		var userAccount = new UserAccount(new Email(USER_EMAIL_STRING), "pass", "admin", UserRole.ASSOCIATION);
		String token = jwtService.generateToken(userAccount);
		String role = jwtService.extractUserRole(token);
		assertThat(role).isEqualTo("ASSOCIATION");
	}

	@Test
	void isTokenValidShouldReturnTrueWhenThereIsMatchingUser() {
		var userAccount = new UserAccount(new Email(USER_EMAIL_STRING), "pass", "validUser", UserRole.PLAYER);
		String token = jwtService.generateToken(userAccount);
		UserDetails userDetails = User.withUsername(USER_EMAIL_STRING).password("password").roles("PLAYER").build();
		assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
	}

	@Test
	void isTokenValidShouldReturnFalseWhenThereIsNotMatchingUser() {
		var userAccount = new UserAccount(new Email(USER_EMAIL_STRING), "pass", "alice", UserRole.PLAYER);
		String token = jwtService.generateToken(userAccount);
		UserDetails userDetails = User.withUsername("invalid@domain.com").password("password").roles("PLAYER").build();
		assertThat(jwtService.isTokenValid(token, userDetails)).isFalse();
	}

}