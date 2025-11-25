package boardgameconnect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TableTest {

	@Test
	void testCreateEmailWhenEmailStringIsValidShouldCreateEmail() {
		Table t = new Table(TableSize.SMALL);
		assertEquals("SMALL", t.getSize());
	}
}
