package boardgameconnect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UserTest {
	
	@Test
    void shouldCreateUserWithEmailAndPassword() {
        User u = new User(new Email("user@domain.com"), "password");
        assertEquals("user@domain.com", u.getEmail().getEmail());
        assertEquals("password", u.getPassword());
    }
}
