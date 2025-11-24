package boardgameconnect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PlayerTest {

    @Test
    void shouldCreatePlayer() {
        Player p = new Player(new Email("p@ex.com"), "pwd", "user1");
        assertEquals("user1", p.getUsername());
    }

}
