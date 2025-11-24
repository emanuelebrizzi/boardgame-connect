package boardgameconnect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PlayerTest {

    @Test
    void shouldCreatePlayer() {
        Player p = new Player(new Email("breezen@domain.com"), "password", "Breezen");
        assertEquals("Breezen", p.getUsername());
    }


}
