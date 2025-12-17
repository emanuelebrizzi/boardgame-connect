package boardgameconnect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EmailTest {

	@Test
	void testCreateEmailWhenEmailStringIsValidShouldCreateEmail() {
		Email email = new Email("user@domain.com");
		assertEquals("user@domain.com", email.getEmail());
	}

	@Test
	void testCreateEmailWhenEmailStringIsNullStringShouldSThrow() {
		assertThrows(IllegalArgumentException.class, () -> new Email(null), "Invalid email format");
	}

	@Test
	void testCreateEmailWhenEmailStringIsEmptyStringShouldSThrow() {
		assertThrows(IllegalArgumentException.class, () -> new Email(""), "Invalid email format");
	}

	@Test
	void testCreateEmailWhenEmailStringIsInvalidEmailShouldSThrow() {
		assertThrows(IllegalArgumentException.class, () -> new Email("Invalidmail"), "Invalid email format");
		assertThrows(IllegalArgumentException.class, () -> new Email("Invalidmail@invaliddomain"),
				"Invalid email format");
		assertThrows(IllegalArgumentException.class, () -> new Email("Invalidmail@invalid domain.com"),
				"Invalid email format");
	}

	@Test
	void testEqualsWhenEmailsWithDifferOnlyByCaseShouldReturnTrue() {
		Email email1 = new Email("USER@DOMAIN.COM");
		Email email2 = new Email("user@domain.com");
		assertTrue(email1.equals(email2));
	}

}
