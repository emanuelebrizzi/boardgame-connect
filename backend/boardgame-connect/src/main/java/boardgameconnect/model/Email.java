package boardgameconnect.model;

public class Email {

	private String email;

	public Email(String email) {
		super();
		if ((email == null) || (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))) {
			throw new IllegalArgumentException("Invalid email format");
		}
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public int hashCode() {
		return email == null ? 0 : email.toLowerCase().hashCode();
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
		if (email == null && other.email == null)
			return true;
		if (email == null || other.email == null)
			return false;

		return email.equalsIgnoreCase(other.email);
	}
}
