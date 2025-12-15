package boardgameconnect.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import boardgameconnect.model.Email;

class EmailConverterTest {

    @Test
    void testConvertToDatabaseColumnWhenEmailIsValidReturnsEmailString() {
	var converter = new EmailConverter();
	var email = new Email("example@mail.com");
	String dbData = converter.convertToDatabaseColumn(email);
	assertThat(dbData).isEqualTo("example@mail.com");
    }

    @Test
    void testConvertToEntityAttributeWhenStringIsPresentReturnsEmailObject() {
	var converter = new EmailConverter();
	String dbData = "dice@example.com";
	Email email = converter.convertToEntityAttribute(dbData);
	assertThat(email.getEmail()).isEqualTo("dice@example.com");
    }

}
