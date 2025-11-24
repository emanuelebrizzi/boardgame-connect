package boardgameconnect.model;

public class User {
	private Email email;
	private String password;
	
	public User(Email email, String password) {
		super();
		this.email = email;
		this.password = password;
        if (password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
	}

	public Email getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}


}
