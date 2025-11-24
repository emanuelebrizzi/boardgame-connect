package boardgameconnect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
		assertThrows(IllegalArgumentException.class, () -> new Email("Invalidmail@invaliddomain"), "Invalid email format");
	}


}
