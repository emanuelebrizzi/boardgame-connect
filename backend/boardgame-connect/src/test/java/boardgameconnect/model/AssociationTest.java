package boardgameconnect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class AssociationTest {
	
	@Test
	void testCreateAssosationWhenNameTaxcodeAddressAreValidShouldCreateassociation() {
		Association a = new Association (new Email("breezen@domain.com"), "password", "Associazione Ludica", "80012354879","Via Roma 1");
		assertEquals("Associazione Ludica", a.getName());
		assertEquals("80012354879", a.getTaxCode());
		assertEquals("Via Roma 1", a.getAddress());
	}

}
