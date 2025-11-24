package boardgameconnect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PlayerTest {

	@Test
	void testCreatePlayerWhenUsernameIsValisShouldCreatePlayer() {
		Player p = new Player(new Email("breezen@domain.com"), "password", "Breezen");
		assertEquals("Breezen", p.getUsername());
	}

	@Test
	void testCreatePlayerWhenUsernameIsNullShouldThrow() {
		assertThrows(IllegalArgumentException.class,
				() -> new Player(new Email("breezen@domain.com"), "password", null), "");
	}

}
