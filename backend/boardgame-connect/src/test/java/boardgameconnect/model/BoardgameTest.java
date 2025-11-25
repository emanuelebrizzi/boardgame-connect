package boardgameconnect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class BoardgameTest {
	
	@Test
	void testCreateBoardgameWhenNameTaxcodeAddressAreValidShouldCreateassociation() {
		Boardgame bg = new Boardgame ("Terra Mystica",2,5,60,30);
		assertEquals("Terra Mystica", bg.getName());
		assertEquals(2, bg.getMinPlayer());
		assertEquals(3, bg.getMaxPlayer());
		assertEquals(60, bg.getMinTime());
		assertEquals(30, bg.getTimePerPlayer());
	}


}
