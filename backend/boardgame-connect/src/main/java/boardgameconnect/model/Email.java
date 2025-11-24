package boardgameconnect.model;

public class Email {

	private String email;

	public Email(String email) {
		super();
		if (email == "") {
			throw new IllegalArgumentException("Invalid email format");
		}
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

}
