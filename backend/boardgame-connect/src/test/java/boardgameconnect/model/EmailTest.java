package boardgameconnect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class EmailTest {

	@Test
    void testCreateEmailWhenEmailStringIsValidShouldCreateEmail() {
        Email email = new Email("user@domain.com");
        assertEquals("user@domain.com", email.getEmail());
    }
}
