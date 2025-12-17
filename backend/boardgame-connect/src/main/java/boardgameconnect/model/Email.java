package boardgameconnect.model;

import java.util.regex.Pattern;

public class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private String email;

    public Email(String email) {
	if ((email == null) || (!EMAIL_PATTERN.matcher(email).matches())) {
	    throw new IllegalArgumentException("Invalid email format");
	}
	this.email = email;
    }

    public String getEmail() {
	return email;
    }

    @Override
    public int hashCode() {
	return email.toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Email other = (Email) obj;
	return email.equalsIgnoreCase(other.email);
    }

    @Override
    public String toString() {
	return email;
    }
}
