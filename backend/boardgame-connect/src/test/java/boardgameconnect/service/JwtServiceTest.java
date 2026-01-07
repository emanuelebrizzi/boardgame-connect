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
		var userAccount = new UserAccount(new Email("user@domain.com"), "encoded_password", "username",
				UserRole.PLAYER);

		String token = jwtService.generateToken(userAccount);
		Claims claims = Jwts.parserBuilder().setSigningKey(jwtService.getSecretKey()).build().parseClaimsJws(token)
				.getBody();

		assertThat(userAccount.getEmail().toString()).isEqualTo(claims.getSubject());
		assertThat(userAccount.getUserRole().toString()).isEqualTo(claims.get("role"));
	}

	@Test
	void extractSubjectShouldReturnCorrectSubject() {
		var userAccount = new UserAccount(new Email("test@domain.com"), "pass", "user", UserRole.ASSOCIATION);
		String token = jwtService.generateToken(userAccount);
		String extractedUsername = jwtService.extractSubject(token);
		assertThat(extractedUsername).isEqualTo("test@domain.com");
	}

	@Test
	void extractUserRoleShouldReturnCorrectRole() {
		var userAccount = new UserAccount(new Email("admin@domain.com"), "pass", "admin", UserRole.ASSOCIATION);
		String token = jwtService.generateToken(userAccount);
		String role = jwtService.extractUserRole(token);
		assertThat(role).isEqualTo("ASSOCIATION");
	}

//	@Test
//	void isTokenValidShouldReturnTrueForMatchingUser() {
//		// Arrange
//		var userAccount = new UserAccount(new Email("valid@domain.com"), "pass", "validUser", UserRole.PLAYER);
//		String token = jwtService.generateToken(userAccount);
//
//		// Mock UserDetails (simulating the user loaded from DB)
//		UserDetails userDetails = new Userdetails
//
//		// Act
//		boolean isValid = jwtService.isTokenValid(token, userDetails);
//
//		// Assert
//		assertThat(isValid).isTrue();
//	}
//
//	@Test
//	void isTokenValidShouldReturnFalseForNonMatchingUser() {
//		// Arrange
//		var userAccount = new UserAccount(new Email("alice@domain.com"), "pass", "alice", UserRole.PLAYER);
//		String token = jwtService.generateToken(userAccount);
//
//		// Mock UserDetails with a DIFFERENT email
//		UserDetails userDetails = mock(UserDetails.class);
//		when(userDetails.getUsername()).thenReturn("bob@domain.com");
//
//		// Act
//		boolean isValid = jwtService.isTokenValid(token, userDetails);
//
//		// Assert
//		assertThat(isValid).isFalse();
//	}

}