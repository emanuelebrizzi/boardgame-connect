package boardgameconnect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class UserTest {

	@Test
	void testCreateUserWhenEmailAndPasswordAreValidShouldCreateUser() {
		User u = new User(new Email("user@domain.com"), "password");
		assertEquals("user@domain.com", u.getEmail().getEmail());
		assertEquals("password", u.getPassword());
	}

	@Test
	void testCreateUserWhenPasswordAreInvalidShouldThrowException() {
		assertThrows(IllegalArgumentException.class, () -> new User(new Email("test@example.com"), ""));
	}
}
