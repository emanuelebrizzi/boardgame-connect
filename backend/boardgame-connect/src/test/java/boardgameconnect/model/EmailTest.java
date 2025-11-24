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
	void testCreateEmailWhenEmailStringIsEmptyStringShouldSThrow() {
        Email email = new Email("");
        assertThrows(IllegalArgumentException.class, () -> new Email(""));
    }
	
	
}
